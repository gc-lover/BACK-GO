package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.InvestmentDividendEntity;
import com.necpgame.backjava.entity.InvestmentEntity;
import com.necpgame.backjava.entity.InvestmentEntity.InvestmentStatus;
import com.necpgame.backjava.entity.InvestmentFundEntity;
import com.necpgame.backjava.entity.InvestmentOpportunityEntity;
import com.necpgame.backjava.entity.InvestmentOpportunityEntity.OpportunityType;
import com.necpgame.backjava.entity.InvestmentOpportunityEntity.RiskLevel;
import com.necpgame.backjava.model.CalculateROIRequest;
import com.necpgame.backjava.model.GetInvestmentFunds200Response;
import com.necpgame.backjava.model.GetInvestmentOpportunities200Response;
import com.necpgame.backjava.model.InvestRequest;
import com.necpgame.backjava.model.Investment;
import com.necpgame.backjava.model.InvestmentDetailed;
import com.necpgame.backjava.model.InvestmentDetailedAllOfDividendHistory;
import com.necpgame.backjava.model.InvestmentFund;
import com.necpgame.backjava.model.InvestmentOpportunity;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.Portfolio;
import com.necpgame.backjava.model.PortfolioAnalysis;
import com.necpgame.backjava.model.PortfolioAnalysisPerformanceMetrics;
import com.necpgame.backjava.model.PortfolioAnalysisPerformanceMetricsBestPerformer;
import com.necpgame.backjava.model.PortfolioAnalysisRiskBreakdown;
import com.necpgame.backjava.model.PortfolioInvestmentsByTypeValue;
import com.necpgame.backjava.model.ROICalculation;
import com.necpgame.backjava.model.WithdrawInvestment200Response;
import com.necpgame.backjava.model.WithdrawInvestmentRequest;
import com.necpgame.backjava.repository.InvestmentDividendRepository;
import com.necpgame.backjava.repository.InvestmentFundRepository;
import com.necpgame.backjava.repository.InvestmentOpportunityRepository;
import com.necpgame.backjava.repository.InvestmentRepository;
import com.necpgame.backjava.service.InvestmentsService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvestmentsServiceImpl implements InvestmentsService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_EARLY_WITHDRAWAL_PENALTY = BigDecimal.valueOf(0.07);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final BigDecimal MINIMUM_ANALYSIS_BASE = BigDecimal.valueOf(1);

    private final InvestmentOpportunityRepository opportunityRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentDividendRepository dividendRepository;
    private final InvestmentFundRepository fundRepository;

    @Override
    public GetInvestmentOpportunities200Response getInvestmentOpportunities(
        String type,
        String riskLevel,
        BigDecimal minRoi,
        int page,
        int pageSize
    ) {
        int resolvedPage = Math.max(page, DEFAULT_PAGE) - 1;
        int resolvedSize = Math.min(Math.max(pageSize, DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);

        Specification<InvestmentOpportunityEntity> spec = Specification.where(null);
        if (StringUtils.hasText(type)) {
            OpportunityType parsedType = parseType(type);
            spec = spec.and((root, query, builder) -> builder.equal(root.get("type"), parsedType));
        }
        if (StringUtils.hasText(riskLevel)) {
            RiskLevel parsedRisk = parseRisk(riskLevel);
            spec = spec.and((root, query, builder) -> builder.equal(root.get("riskLevel"), parsedRisk));
        }
        if (minRoi != null) {
            BigDecimal threshold = minRoi.setScale(2, RoundingMode.HALF_UP);
            spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("expectedRoiPercent"), threshold));
        }

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, Sort.by(Sort.Direction.DESC, "expectedRoiPercent"));
        Page<InvestmentOpportunityEntity> opportunities = opportunityRepository.findAll(spec, pageable);

        List<InvestmentOpportunity> data = opportunities.stream()
            .map(this::mapOpportunity)
            .toList();

        PaginationMeta meta = new PaginationMeta()
            .page(opportunities.getNumber() + 1)
            .pageSize(opportunities.getSize())
            .total(Math.toIntExact(opportunities.getTotalElements()))
            .totalPages(opportunities.getTotalPages())
            .hasNext(opportunities.hasNext())
            .hasPrev(opportunities.hasPrevious());

        return new GetInvestmentOpportunities200Response()
            .data(new ArrayList<>(data))
            .meta(meta);
    }

    @Override
    @Transactional
    public Investment invest(InvestRequest investRequest) {
        if (investRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (investRequest.getCharacterId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Character id is required");
        }
        if (!StringUtils.hasText(investRequest.getOpportunityId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opportunity id is required");
        }
        if (investRequest.getAmount() == null || investRequest.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        UUID opportunityId = parseUuid(investRequest.getOpportunityId());
        InvestmentOpportunityEntity opportunity = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investment opportunity not found"));

        BigDecimal amount = BigDecimal.valueOf(investRequest.getAmount()).setScale(2, RoundingMode.HALF_UP);
        validateInvestmentAmount(opportunity, amount);

        if (!opportunity.hasAvailableSlots()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No available slots for the selected opportunity");
        }

        try {
            opportunity.allocateSlot();
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime maturity = now.plusDays(Optional.ofNullable(opportunity.getDurationDays()).orElse(0));

        InvestmentEntity entity = InvestmentEntity.builder()
            .characterId(investRequest.getCharacterId())
            .opportunity(opportunity)
            .amountInvested(amount)
            .currentValue(amount)
            .expectedRoiPercent(opportunity.getExpectedRoiPercent())
            .dividendsTotal(ZERO)
            .status(InvestmentStatus.ACTIVE)
            .createdAt(now)
            .maturityDate(maturity)
            .build();

        InvestmentEntity saved = investmentRepository.save(entity);
        opportunityRepository.save(opportunity);
        log.info("Character {} invested {} credits into opportunity {}", investRequest.getCharacterId(), amount, opportunityId);
        return mapInvestment(saved);
    }

    @Override
    public InvestmentDetailed getInvestment(UUID investmentId) {
        InvestmentEntity investment = investmentRepository.findById(investmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investment not found"));
        List<InvestmentDividendEntity> dividends = dividendRepository.findAllByInvestmentIdOrderByPaidAtAsc(investmentId);
        return mapInvestmentDetailed(investment, dividends);
    }

    @Override
    @Transactional
    public WithdrawInvestment200Response withdrawInvestment(UUID investmentId, WithdrawInvestmentRequest request) {
        InvestmentEntity investment = investmentRepository.lockById(investmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investment not found"));

        BigDecimal preWithdrawalValue = investment.getCurrentValue();
        if (preWithdrawalValue.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investment already withdrawn");
        }

        Integer requestedAmount = request != null && request.getAmount() != null && request.getAmount().isPresent()
            ? request.getAmount().get()
            : null;

        BigDecimal withdrawAmount = requestedAmount == null
            ? preWithdrawalValue
            : BigDecimal.valueOf(requestedAmount).setScale(2, RoundingMode.HALF_UP);

        if (withdrawAmount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Withdrawal amount must be positive");
        }
        if (withdrawAmount.compareTo(preWithdrawalValue) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Withdrawal amount exceeds current value");
        }

        boolean earlyWithdrawal = investment.getMaturityDate() != null && investment.getMaturityDate().isAfter(OffsetDateTime.now())
            && investment.getStatus() == InvestmentStatus.ACTIVE;

        BigDecimal penaltyRate = earlyWithdrawal ? DEFAULT_EARLY_WITHDRAWAL_PENALTY : BigDecimal.ZERO;
        BigDecimal penalty = withdrawAmount.multiply(penaltyRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAmount = withdrawAmount.subtract(penalty).max(ZERO);

        BigDecimal updatedValue = preWithdrawalValue.subtract(withdrawAmount).max(ZERO);
        BigDecimal amountInvestedBefore = investment.getAmountInvested();

        investment.setCurrentValue(updatedValue);
        if (updatedValue.signum() == 0) {
            investment.setAmountInvested(ZERO);
            investment.setStatus(InvestmentStatus.WITHDRAWN);
            investment.getOpportunity().releaseSlot();
        } else {
            BigDecimal ratio = withdrawAmount.divide(preWithdrawalValue, 6, RoundingMode.HALF_UP);
            BigDecimal remainingInvested = amountInvestedBefore.multiply(BigDecimal.ONE.subtract(ratio)).max(ZERO);
            investment.setAmountInvested(remainingInvested);
        }
        investment.setLastEvaluatedAt(OffsetDateTime.now());

        investmentRepository.save(investment);
        opportunityRepository.save(investment.getOpportunity());

        BigDecimal roiPercent = computeRoiPercent(preWithdrawalValue, amountInvestedBefore);
        WithdrawInvestment200Response response = new WithdrawInvestment200Response()
            .withdrawnAmount(toInt(withdrawAmount))
            .penalty(toInt(penalty))
            .netAmount(toInt(netAmount))
            .roi(roiPercent.floatValue());

        log.info("Investment {} withdrawal processed: amount={}, penalty={}, net={}, early={}",
            investmentId, withdrawAmount, penalty, netAmount, earlyWithdrawal);
        return response;
    }

    @Override
    public Portfolio getPortfolio(UUID characterId) {
        List<InvestmentEntity> investments = investmentRepository.findAllByCharacterId(characterId);
        BigDecimal totalInvested = investments.stream()
            .map(InvestmentEntity::getAmountInvested)
            .reduce(ZERO, BigDecimal::add);
        BigDecimal totalCurrentValue = investments.stream()
            .map(InvestmentEntity::getCurrentValue)
            .reduce(ZERO, BigDecimal::add);
        BigDecimal profitLoss = totalCurrentValue.subtract(totalInvested);
        BigDecimal roi = totalInvested.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);

        Map<String, PortfolioInvestmentsByTypeValue> byType = investments.stream()
            .collect(Collectors.groupingBy(
                entity -> entity.getOpportunity().getType().name(),
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    BigDecimal typeTotal = list.stream()
                        .map(InvestmentEntity::getCurrentValue)
                        .reduce(ZERO, BigDecimal::add);
                    return new PortfolioInvestmentsByTypeValue()
                        .count(list.size())
                        .totalValue(toInt(typeTotal));
                })
            ));

        List<Investment> investmentDtos = investments.stream()
            .map(this::mapInvestment)
            .toList();

        return new Portfolio()
            .characterId(characterId)
            .totalInvested(toInt(totalInvested))
            .totalCurrentValue(toInt(totalCurrentValue))
            .totalProfitLoss(toInt(profitLoss))
            .overallRoi(roi.floatValue())
            .investments(new ArrayList<>(investmentDtos))
            .investmentsByType(byType);
    }

    @Override
    public PortfolioAnalysis getPortfolioAnalysis(UUID characterId) {
        List<InvestmentEntity> investments = investmentRepository.findAllByCharacterId(characterId);
        if (investments.isEmpty()) {
            return new PortfolioAnalysis()
                .characterId(characterId)
                .riskScore(0.0f)
                .diversificationScore(0.0f)
                .recommendations(List.of("Добавьте инвестиции для формирования портфеля"));
        }

        BigDecimal totalValue = investments.stream()
            .map(InvestmentEntity::getCurrentValue)
            .reduce(ZERO, BigDecimal::add);
        BigDecimal safeTotal = totalValue.max(MINIMUM_ANALYSIS_BASE);

        PortfolioAnalysisRiskBreakdown riskBreakdown = buildRiskBreakdown(investments, safeTotal);
        float riskScore = computeRiskScore(investments, safeTotal);
        float diversificationScore = computeDiversificationScore(investments);
        List<String> recommendations = buildRecommendations(riskScore, diversificationScore);

        PortfolioAnalysisPerformanceMetrics metrics = buildPerformanceMetrics(investments);

        return new PortfolioAnalysis()
            .characterId(characterId)
            .riskScore(riskScore)
            .diversificationScore(diversificationScore)
            .riskBreakdown(riskBreakdown)
            .performanceMetrics(metrics)
            .recommendations(recommendations);
    }

    @Override
    public ROICalculation calculateRoi(CalculateROIRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        InvestmentOpportunityEntity opportunity = null;
        if (StringUtils.hasText(request.getOpportunityId())) {
            UUID opportunityId = parseUuid(request.getOpportunityId());
            opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investment opportunity not found"));
        }

        int amountValue = Optional.ofNullable(request.getAmount())
            .orElseGet(() -> opportunity != null ? toInt(opportunity.getMinInvestment()) : 1000);
        if (amountValue <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        int duration = Optional.ofNullable(request.getDurationDays())
            .orElseGet(() -> opportunity != null ? Optional.ofNullable(opportunity.getDurationDays()).orElse(30) : 30);

        BigDecimal principal = BigDecimal.valueOf(amountValue).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roiPercent = opportunity != null ? opportunity.getExpectedRoiPercent() : BigDecimal.valueOf(12.0);
        int baseDuration = opportunity != null ? Optional.ofNullable(opportunity.getDurationDays()).orElse(duration) : duration;

        BigDecimal durationFactor = BigDecimal.valueOf(duration)
            .divide(BigDecimal.valueOf(Math.max(baseDuration, 1)), 6, RoundingMode.HALF_UP);
        BigDecimal expectedReturn = principal.multiply(roiPercent)
            .multiply(durationFactor)
            .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

        BigDecimal riskAdjusted = adjustForRisk(roiPercent, opportunity != null ? opportunity.getRiskLevel() : RiskLevel.MEDIUM);
        String confidence = deriveConfidence(opportunity != null ? opportunity.getRiskLevel() : RiskLevel.MEDIUM);

        return new ROICalculation()
            .opportunityId(opportunity != null ? opportunity.getId().toString() : null)
            .investmentAmount(toInt(principal))
            .durationDays(duration)
            .expectedReturn(toInt(expectedReturn))
            .expectedRoi(roiPercent.floatValue())
            .riskAdjustedRoi(riskAdjusted.floatValue())
            .confidenceLevel(confidence);
    }

    @Override
    public GetInvestmentFunds200Response getInvestmentFunds() {
        List<InvestmentFund> funds = fundRepository.findAllByOrderByPerformanceYtdDesc().stream()
            .map(this::mapFund)
            .toList();
        return new GetInvestmentFunds200Response().funds(new ArrayList<>(funds));
    }

    private OpportunityType parseType(String type) {
        try {
            return OpportunityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown opportunity type: " + type);
        }
    }

    private RiskLevel parseRisk(String risk) {
        try {
            return RiskLevel.valueOf(risk.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown risk level: " + risk);
        }
    }

    private void validateInvestmentAmount(InvestmentOpportunityEntity opportunity, BigDecimal amount) {
        if (amount.compareTo(opportunity.getMinInvestment()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount is below minimum investment threshold");
        }
        if (opportunity.getMaxInvestment() != null && amount.compareTo(opportunity.getMaxInvestment()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount exceeds maximum investment threshold");
        }
    }

    private InvestmentOpportunity mapOpportunity(InvestmentOpportunityEntity entity) {
        InvestmentOpportunity model = new InvestmentOpportunity()
            .opportunityId(entity.getId().toString())
            .name(entity.getName())
            .type(InvestmentOpportunity.TypeEnum.fromValue(entity.getType().name()))
            .description(entity.getDescription())
            .minInvestment(toInt(entity.getMinInvestment()))
            .expectedRoi(entity.getExpectedRoiPercent().floatValue())
            .riskLevel(InvestmentOpportunity.RiskLevelEnum.valueOf(entity.getRiskLevel().name()))
            .durationDays(entity.getDurationDays())
            .availableSlots(entity.getAvailableSlots());

        if (entity.getMaxInvestment() != null) {
            model.setMaxInvestment(JsonNullable.of(toInt(entity.getMaxInvestment())));
        }
        return model;
    }

    private Investment mapInvestment(InvestmentEntity entity) {
        return new Investment()
            .investmentId(entity.getId())
            .characterId(entity.getCharacterId())
            .opportunityId(entity.getOpportunity().getId().toString())
            .type(entity.getOpportunity().getType().name())
            .amountInvested(toInt(entity.getAmountInvested()))
            .currentValue(toInt(entity.getCurrentValue()))
            .status(Investment.StatusEnum.valueOf(entity.getStatus().name()))
            .createdAt(entity.getCreatedAt())
            .maturityDate(entity.getMaturityDate());
    }

    private InvestmentDetailed mapInvestmentDetailed(InvestmentEntity entity, List<InvestmentDividendEntity> dividends) {
        InvestmentOpportunity opportunity = mapOpportunity(entity.getOpportunity());
        List<InvestmentDetailedAllOfDividendHistory> history = dividends.stream()
            .map(dividend -> new InvestmentDetailedAllOfDividendHistory()
                .amount(toInt(dividend.getAmount()))
                .paidAt(dividend.getPaidAt()))
            .toList();

        return new InvestmentDetailed()
            .investmentId(entity.getId())
            .characterId(entity.getCharacterId())
            .opportunityId(entity.getOpportunity().getId().toString())
            .type(entity.getOpportunity().getType().name())
            .amountInvested(toInt(entity.getAmountInvested()))
            .currentValue(toInt(entity.getCurrentValue()))
            .status(InvestmentDetailed.StatusEnum.valueOf(entity.getStatus().name()))
            .createdAt(entity.getCreatedAt())
            .maturityDate(entity.getMaturityDate())
            .opportunityDetails(opportunity)
            .profitLoss(toInt(entity.getProfitLoss()))
            .roiCurrent(computeRoiPercent(entity.getCurrentValue(), entity.getAmountInvested()).floatValue())
            .dividendsReceived(toInt(entity.getDividendsTotal()))
            .dividendHistory(history);
    }

    private InvestmentFund mapFund(InvestmentFundEntity entity) {
        return new InvestmentFund()
            .fundId(entity.getId())
            .name(entity.getName())
            .manager(entity.getManager())
            .type(entity.getType())
            .totalValue(toInt(entity.getTotalValue()))
            .investorsCount(entity.getInvestorsCount())
            .performanceYtd(entity.getPerformanceYtd().floatValue())
            .minInvestment(toInt(entity.getMinInvestment()))
            .managementFee(entity.getManagementFeePercent().floatValue());
    }

    private PortfolioAnalysisRiskBreakdown buildRiskBreakdown(List<InvestmentEntity> investments, BigDecimal totalValue) {
        Map<RiskLevel, BigDecimal> totals = new EnumMap<>(RiskLevel.class);
        for (InvestmentEntity investment : investments) {
            RiskLevel risk = investment.getOpportunity().getRiskLevel();
            totals.merge(risk, investment.getCurrentValue(), BigDecimal::add);
        }

        BigDecimal low = totals.getOrDefault(RiskLevel.LOW, ZERO);
        BigDecimal medium = totals.getOrDefault(RiskLevel.MEDIUM, ZERO);
        BigDecimal high = totals.getOrDefault(RiskLevel.HIGH, ZERO).add(totals.getOrDefault(RiskLevel.VERY_HIGH, ZERO));

        return new PortfolioAnalysisRiskBreakdown()
            .lowRisk(toPercentage(low, totalValue))
            .mediumRisk(toPercentage(medium, totalValue))
            .highRisk(toPercentage(high, totalValue));
    }

    private float computeRiskScore(List<InvestmentEntity> investments, BigDecimal totalValue) {
        Map<RiskLevel, Integer> weights = Map.of(
            RiskLevel.LOW, 2,
            RiskLevel.MEDIUM, 5,
            RiskLevel.HIGH, 8,
            RiskLevel.VERY_HIGH, 10
        );
        BigDecimal weightedSum = investments.stream()
            .map(entity -> entity.getCurrentValue()
                .multiply(BigDecimal.valueOf(weights.get(entity.getOpportunity().getRiskLevel()))))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal score = weightedSum.divide(totalValue.max(MINIMUM_ANALYSIS_BASE), 2, RoundingMode.HALF_UP);
        return score.floatValue();
    }

    private float computeDiversificationScore(List<InvestmentEntity> investments) {
        long types = investments.stream()
            .map(entity -> entity.getOpportunity().getType())
            .distinct()
            .count();
        long count = investments.size();
        float scoreByType = Math.min(1.0f, types / 5.0f);
        float scoreByCount = Math.min(1.0f, count / 10.0f);
        return (scoreByType * 0.6f + scoreByCount * 0.4f) * 100f;
    }

    private List<String> buildRecommendations(float riskScore, float diversificationScore) {
        List<String> recommendations = new ArrayList<>();
        if (riskScore > 7.5f) {
            recommendations.add("Снизьте долю высокорискованных инвестиций для стабилизации портфеля");
        }
        if (diversificationScore < 40f) {
            recommendations.add("Добавьте инвестиции других типов для повышения диверсификации");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Портфель сбалансирован. Продолжайте отслеживать доходность и риски.");
        }
        return recommendations;
    }

    private PortfolioAnalysisPerformanceMetrics buildPerformanceMetrics(List<InvestmentEntity> investments) {
        Comparator<InvestmentEntity> comparator = Comparator.comparing(this::computeCurrentRoiBigDecimal);
        InvestmentEntity best = investments.stream().max(comparator).orElse(null);
        InvestmentEntity worst = investments.stream().min(comparator).orElse(null);

        PortfolioAnalysisPerformanceMetrics metrics = new PortfolioAnalysisPerformanceMetrics();
        if (best != null) {
            metrics.setBestPerformer(new PortfolioAnalysisPerformanceMetricsBestPerformer()
                .investmentId(best.getId())
                .roi(computeCurrentRoiBigDecimal(best).floatValue()));
        }
        if (worst != null) {
            metrics.setWorstPerformer(new PortfolioAnalysisPerformanceMetricsBestPerformer()
                .investmentId(worst.getId())
                .roi(computeCurrentRoiBigDecimal(worst).floatValue()));
        }
        return metrics;
    }

    private BigDecimal computeCurrentRoiBigDecimal(InvestmentEntity entity) {
        if (entity.getAmountInvested().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return entity.getCurrentValue()
            .subtract(entity.getAmountInvested())
            .divide(entity.getAmountInvested(), 4, RoundingMode.HALF_UP)
            .multiply(ONE_HUNDRED);
    }

    private BigDecimal computeRoiPercent(BigDecimal currentValue, BigDecimal invested) {
        if (invested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(invested)
            .divide(invested, 4, RoundingMode.HALF_UP)
            .multiply(ONE_HUNDRED);
    }

    private BigDecimal adjustForRisk(BigDecimal roi, RiskLevel riskLevel) {
        BigDecimal penalty = switch (riskLevel) {
            case LOW -> BigDecimal.valueOf(0.5);
            case MEDIUM -> BigDecimal.valueOf(1.5);
            case HIGH -> BigDecimal.valueOf(2.5);
            case VERY_HIGH -> BigDecimal.valueOf(3.5);
        };
        return roi.subtract(penalty).max(BigDecimal.ZERO);
    }

    private String deriveConfidence(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case LOW -> "HIGH";
            case MEDIUM -> "MEDIUM";
            case HIGH, VERY_HIGH -> "LOW";
        };
    }

    private float toPercentage(BigDecimal value, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0f;
        }
        return value.divide(total, 4, RoundingMode.HALF_UP)
            .multiply(ONE_HUNDRED)
            .floatValue();
    }

    private int toInt(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID: " + raw);
        }
    }
}



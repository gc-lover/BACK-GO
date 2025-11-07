package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.VoiceChannelEntity;
import com.necpgame.backjava.entity.VoiceParticipantEntity;
import com.necpgame.backjava.entity.enums.VoiceChannelOwnerType;
import com.necpgame.backjava.entity.enums.VoiceChannelStatus;
import com.necpgame.backjava.entity.enums.VoiceChannelType;
import com.necpgame.backjava.entity.enums.VoiceParticipantAudioQuality;
import com.necpgame.backjava.entity.enums.VoiceParticipantStatus;
import com.necpgame.backjava.entity.enums.VoiceQualityPreset;
import com.necpgame.backjava.model.ProximitySettings;
import com.necpgame.backjava.model.VoiceChannel;
import com.necpgame.backjava.model.VoiceChannelOwner;
import com.necpgame.backjava.model.VoiceChannelPermissions;
import com.necpgame.backjava.model.VoiceChannelSummary;
import com.necpgame.backjava.model.VoiceParticipant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoiceChannelMapper {

    @Mapping(target = "channelId", expression = "java(mapId(entity.getId()))")
    @Mapping(target = "channelType", expression = "java(mapChannelType(entity.getChannelType()))")
    @Mapping(target = "owner", expression = "java(mapOwner(entity))")
    @Mapping(target = "qualityPreset", expression = "java(mapQualityPreset(entity.getQualityPreset()))")
    @Mapping(target = "status", expression = "java(mapStatus(entity.getStatus()))")
    @Mapping(target = "allowedRoles", expression = "java(new ArrayList<>(entity.getAllowedRoles()))")
    @Mapping(target = "permissions", expression = "java(mapPermissions(entity))")
    @Mapping(target = "proximity", expression = "java(mapProximity(entity))")
    @Mapping(target = "analytics", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "maxBitrateKbps", source = "maxBitrateKbps")
    @Mapping(target = "activeParticipants", source = "activeParticipants")
    @Mapping(target = "maxParticipants", source = "maxParticipants")
    @Mapping(target = "autoCloseMinutes", source = "autoCloseMinutes")
    @Mapping(target = "createdAt", source = "createdAt")
    VoiceChannel toDto(VoiceChannelEntity entity);

    @Mapping(target = "channelId", expression = "java(mapId(entity.getId()))")
    @Mapping(target = "channelType", expression = "java(mapChannelType(entity.getChannelType()))")
    @Mapping(target = "status", expression = "java(mapStatus(entity.getStatus()))")
    @Mapping(target = "qualityPreset", expression = "java(mapQualityPreset(entity.getQualityPreset()))")
    @Mapping(target = "owner", expression = "java(mapOwner(entity))")
    @Mapping(target = "activeParticipants", source = "activeParticipants")
    @Mapping(target = "maxParticipants", source = "maxParticipants")
    @Mapping(target = "channelName", source = "channelName")
    VoiceChannelSummary toSummary(VoiceChannelEntity entity);

    List<VoiceChannelSummary> toSummaryList(List<VoiceChannelEntity> entities);

    @Mapping(target = "playerId", source = "playerId")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", expression = "java(mapParticipantStatus(entity.getStatus()))")
    @Mapping(target = "isMuted", source = "muted")
    @Mapping(target = "isDeafened", source = "deafened")
    @Mapping(target = "isSpeaking", source = "speaking")
    @Mapping(target = "audioQuality", expression = "java(mapAudioQuality(entity.getAudioQuality()))")
    @Mapping(target = "joinedAt", source = "joinedAt")
    @Mapping(target = "connectionId", source = "connectionId")
    VoiceParticipant toParticipant(VoiceParticipantEntity entity);

    default VoiceChannel.ChannelTypeEnum mapChannelType(VoiceChannelType channelType) {
        return channelType == null ? null : VoiceChannel.ChannelTypeEnum.fromValue(channelType.name().toLowerCase());
    }

    default VoiceChannel.StatusEnum mapStatus(VoiceChannelStatus status) {
        return status == null ? null : VoiceChannel.StatusEnum.fromValue(status.name().toLowerCase());
    }

    default VoiceChannel.QualityPresetEnum mapQualityPreset(VoiceQualityPreset preset) {
        return preset == null ? null : VoiceChannel.QualityPresetEnum.fromValue(preset.name().toLowerCase());
    }

    default String mapId(UUID id) {
        return id == null ? null : id.toString();
    }

    default VoiceChannelOwner mapOwner(VoiceChannelEntity entity) {
        if (entity.getOwnerType() == null || entity.getOwnerId() == null) {
            return null;
        }
        VoiceChannelOwner owner = new VoiceChannelOwner(mapOwnerType(entity.getOwnerType()), entity.getOwnerId());
        return owner;
    }

    default VoiceChannelPermissions mapPermissions(VoiceChannelEntity entity) {
        if (entity.getPermissions() == null) {
            return null;
        }
        VoiceChannelPermissions permissions = new VoiceChannelPermissions();
        permissions.setAllowInvite(entity.getPermissions().isAllowInvite());
        permissions.setAllowRecording(entity.getPermissions().isAllowRecording());
        permissions.setAllowSpectators(entity.getPermissions().isAllowSpectators());
        return permissions;
    }

    default ProximitySettings mapProximity(VoiceChannelEntity entity) {
        if (entity.getProximity() == null) {
            return null;
        }
        ProximitySettings proximity = new ProximitySettings();
        proximity.setEnabled(entity.getProximity().isEnabled());
        proximity.setFalloffStartMeters(entity.getProximity().getFalloffStartMeters());
        proximity.setFalloffEndMeters(entity.getProximity().getFalloffEndMeters());
        proximity.setSpatialAudio(entity.getProximity().isSpatialAudio());
        return proximity;
    }

    default VoiceParticipant.StatusEnum mapParticipantStatus(VoiceParticipantStatus status) {
        return status == null ? null : VoiceParticipant.StatusEnum.fromValue(status.name().toLowerCase());
    }

    default VoiceParticipant.AudioQualityEnum mapAudioQuality(VoiceParticipantAudioQuality audioQuality) {
        return audioQuality == null ? null : VoiceParticipant.AudioQualityEnum.fromValue(audioQuality.name().toLowerCase());
    }

    default VoiceChannelOwner.OwnerTypeEnum mapOwnerType(VoiceChannelOwnerType ownerType) {
        return ownerType == null ? null : VoiceChannelOwner.OwnerTypeEnum.fromValue(ownerType.name().toLowerCase());
    }
}


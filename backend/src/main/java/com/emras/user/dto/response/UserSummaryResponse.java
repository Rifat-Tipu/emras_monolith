package com.emras.user.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Lightweight user projection exposed via UserFacade.
 * Only contains fields that other domains legitimately need.
 * Never expose the full User entity or UserDetailResponse across domains.
 */
@Getter
@Builder
public class UserSummaryResponse {

    private final Long   id;
    private final String fullName;
    private final String email;
}

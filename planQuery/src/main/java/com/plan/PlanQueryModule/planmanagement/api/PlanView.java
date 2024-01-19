package com.plan.PlanQueryModule.planmanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "A Plan")
public class PlanView {
    @Schema(description = "The ID of the plan")
    private UUID planID;
    @Schema(description = "The name of the plan")
    private String planName;
    @Schema(description = "The number of minutes included in the plan")
    private Integer minutes;
    @Schema(description = "The monthly fee to be paid by subscribers of this plan")
    private BigDecimal monthlyFee;
    @Schema(description = "The annual fee to be paid by subscribers of this plan")
    private BigDecimal annualFee;
    @Schema(description = "The number of devices the user can have associated")
    private Integer deviceNr;
    @Schema(description = "The number of music collections included in the plan")
    private Integer musicCollectionsNr;
    @Schema(description = "Is the recommendation of music automatic or personalized?")
    private String musicRecommendation;
    @Schema(description = "is the plan active?")
    private boolean isActive;
    @Schema(description = "is this plan a Bonus?")
    private boolean isBonus;
}
package com.plan.PlanCommandModule.planmanagement.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequest {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 2048)
    private String planName;

    @NotNull
    @Min(0)
    private Integer minutes;

    @NotNull
    @Min(0)
    private Double monthlyFee;

    @NotNull
    @Min(0)
    private Double annualFee;

    @NotNull
    @Min(0)
    private Integer deviceNr;

    @NotNull
    @Min(0)
    private Integer musicCollectionsNr;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 2048)
    private String musicRecommendation;

}

package com.plan.PlanCommandModule.planmanagement.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPlanRequest {


    @Size(min = 1, max = 2048)
    private String planName;

    @Min(0)
    private Integer minutes;

    @Min(0)
    private Integer deviceNr;

    @Min(0)
    private Double monthlyFee;
    @Min(0)
    private Double annualFee;

    @Min(0)
    private Integer musicCollectionsNr;

    @Size(min = 1, max = 2048)
    private String musicRecommendation;

}

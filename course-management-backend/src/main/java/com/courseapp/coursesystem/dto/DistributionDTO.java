package com.courseapp.coursesystem.dto;

import java.util.List;

public class DistributionDTO {
    private List<String> labels;
    private List<Long> data;

    public DistributionDTO() {}

    public DistributionDTO(List<String> labels, List<Long> data) {
        this.labels = labels;
        this.data = data;
    }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Long> getData() { return data; }
    public void setData(List<Long> data) { this.data = data; }
}

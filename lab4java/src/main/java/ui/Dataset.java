package ui;

import java.util.List;
import java.util.Map;

public record Dataset(
    List<String> header,
    List<Map<String, Double>> data
) {}

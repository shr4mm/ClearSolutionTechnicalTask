package ua.shr4m.ClearSolution.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${app.legalAge}")
    private int legalAge;

    public int getLegalAge() {
        return legalAge;
    }
}

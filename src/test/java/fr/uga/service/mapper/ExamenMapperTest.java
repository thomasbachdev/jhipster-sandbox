package fr.uga.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExamenMapperTest {

    private ExamenMapper examenMapper;

    @BeforeEach
    public void setUp() {
        examenMapper = new ExamenMapperImpl();
    }
}

package fr.uga.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResidentMapperTest {

    private ResidentMapper residentMapper;

    @BeforeEach
    public void setUp() {
        residentMapper = new ResidentMapperImpl();
    }
}

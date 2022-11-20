package fr.uga.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocteurMapperTest {

    private DocteurMapper docteurMapper;

    @BeforeEach
    public void setUp() {
        docteurMapper = new DocteurMapperImpl();
    }
}

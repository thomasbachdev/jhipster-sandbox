package fr.uga.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import fr.uga.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocteurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocteurDTO.class);
        DocteurDTO docteurDTO1 = new DocteurDTO();
        docteurDTO1.setId(1L);
        DocteurDTO docteurDTO2 = new DocteurDTO();
        assertThat(docteurDTO1).isNotEqualTo(docteurDTO2);
        docteurDTO2.setId(docteurDTO1.getId());
        assertThat(docteurDTO1).isEqualTo(docteurDTO2);
        docteurDTO2.setId(2L);
        assertThat(docteurDTO1).isNotEqualTo(docteurDTO2);
        docteurDTO1.setId(null);
        assertThat(docteurDTO1).isNotEqualTo(docteurDTO2);
    }
}

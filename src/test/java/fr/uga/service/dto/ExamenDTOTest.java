package fr.uga.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import fr.uga.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamenDTO.class);
        ExamenDTO examenDTO1 = new ExamenDTO();
        examenDTO1.setId(1L);
        ExamenDTO examenDTO2 = new ExamenDTO();
        assertThat(examenDTO1).isNotEqualTo(examenDTO2);
        examenDTO2.setId(examenDTO1.getId());
        assertThat(examenDTO1).isEqualTo(examenDTO2);
        examenDTO2.setId(2L);
        assertThat(examenDTO1).isNotEqualTo(examenDTO2);
        examenDTO1.setId(null);
        assertThat(examenDTO1).isNotEqualTo(examenDTO2);
    }
}

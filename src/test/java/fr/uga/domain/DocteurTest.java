package fr.uga.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.uga.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocteurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Docteur.class);
        Docteur docteur1 = new Docteur();
        docteur1.setId(1L);
        Docteur docteur2 = new Docteur();
        docteur2.setId(docteur1.getId());
        assertThat(docteur1).isEqualTo(docteur2);
        docteur2.setId(2L);
        assertThat(docteur1).isNotEqualTo(docteur2);
        docteur1.setId(null);
        assertThat(docteur1).isNotEqualTo(docteur2);
    }
}

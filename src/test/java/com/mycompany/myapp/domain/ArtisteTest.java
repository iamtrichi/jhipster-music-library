package com.mycompany.myapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mycompany.myapp.web.rest.TestUtil;

class ArtisteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artiste.class);
        Artiste artiste1 = new Artiste();
        artiste1.setId(1L);
        Artiste artiste2 = new Artiste();
        artiste2.setId(artiste1.getId());
        assertThat(artiste1).isEqualTo(artiste2);
        artiste2.setId(2L);
        assertThat(artiste1).isNotEqualTo(artiste2);
        artiste1.setId(null);
        assertThat(artiste1).isNotEqualTo(artiste2);
    }
}

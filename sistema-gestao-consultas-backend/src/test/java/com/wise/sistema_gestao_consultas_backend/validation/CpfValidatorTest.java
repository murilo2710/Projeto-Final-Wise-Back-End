package com.wise.sistema_gestao_consultas_backend.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CpfValidatorTest {

    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
    }

    @Test
    void deveAceitarCpfValidoSemFormatacao() {
        assertTrue(validator.isValid("93541134780", null));
    }

    @Test
    void deveAceitarCpfValidoComFormatacao() {
        assertTrue(validator.isValid("935.411.347-80", null));
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorInvalido() {
        assertFalse(validator.isValid("93541134781", null));
    }

    @Test
    void deveRejeitarCpfComTodosDigitosIguais() {
        assertFalse(validator.isValid("11111111111", null));
    }

    @Test
    void deveRejeitarCpfComQuantidadeInvalidaDeDigitos() {
        assertFalse(validator.isValid("123456789", null));
    }

    @Test
    void deveAceitarNuloOuVazioParaPermitirValidacaoDeObrigatoriedadeSeparada() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }
}

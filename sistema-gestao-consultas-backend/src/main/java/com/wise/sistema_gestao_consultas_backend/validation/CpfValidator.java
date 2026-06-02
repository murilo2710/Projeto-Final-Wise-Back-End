package com.wise.sistema_gestao_consultas_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CpfValido, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }

        String apenasDigitos = cpf.replaceAll("\\D", "");

        if (apenasDigitos.length() != 11 || todosDigitosIguais(apenasDigitos)) {
            return false;
        }

        int primeiroDigito = calcularDigito(apenasDigitos, 9, 10);
        int segundoDigito = calcularDigito(apenasDigitos, 10, 11);

        return Character.getNumericValue(apenasDigitos.charAt(9)) == primeiroDigito
                && Character.getNumericValue(apenasDigitos.charAt(10)) == segundoDigito;
    }

    private boolean todosDigitosIguais(String cpf) {
        char primeiro = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private int calcularDigito(String cpf, int quantidadeDigitos, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}

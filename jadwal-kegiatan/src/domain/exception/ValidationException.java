package domain.exception;

/**
 * Exception yang dilempar saat terjadi kegagalan validasi aturan bisnis atau input domain.
 * Pesannya sudah berupa kalimat siap tampil (tanpa prefix "[!]").
 */
public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}

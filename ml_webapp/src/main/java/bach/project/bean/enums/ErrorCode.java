package bach.project.bean.enums;

public enum ErrorCode {
    WRONG_USERNAME(0), NOT_ACTIVATED(1), WRONG_PASSWORD(2),INVALID_TOKEN(3);

    private int status;

    private ErrorCode(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static ErrorCode getCodeByStatus(int status) {
        switch (status) {
            case 0:
                return WRONG_USERNAME;
            case 1:
                return NOT_ACTIVATED;
            case 2:
                return WRONG_PASSWORD;
            case 3:
                return INVALID_TOKEN;
            default:
                return null;
        }
    }
}
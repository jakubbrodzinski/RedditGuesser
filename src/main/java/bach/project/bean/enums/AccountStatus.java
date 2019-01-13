package bach.project.bean.enums;

public enum AccountStatus {
    OK(1), ACTIVATED(2), CHANGED_PASSWORD(3), TOKEN_SENT(4), LOGOUT(5);

    private int status;

    private AccountStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static AccountStatus getAccountStatusByInteger(int status) {
        switch (status) {
            case 1:
                return OK;
            case 2:
                return ACTIVATED;
            case 3:
                return CHANGED_PASSWORD;
            case 4:
                return TOKEN_SENT;
            case 5:
                return LOGOUT;
            default:
                return null;
        }
    }
}

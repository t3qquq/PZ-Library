// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

public class DBTicket {
    private String author = null;
    private String message = "";
    private int ticketID = 0;
    private boolean viewed = false;
    private DBTicket answer = null;
    private boolean isAnswer = false;

    public DBTicket(String _author, String _message, int _ticketID) {
        this.author = _author;
        this.message = _message;
        this.ticketID = _ticketID;
        this.viewed = this.viewed;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String _author) {
        this.author = _author;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String _message) {
        this.message = _message;
    }

    public int getTicketID() {
        return this.ticketID;
    }

    public void setTicketID(int _ticketID) {
        this.ticketID = _ticketID;
    }

    public boolean isViewed() {
        return this.viewed;
    }

    public void setViewed(boolean _viewed) {
        this.viewed = _viewed;
    }

    public DBTicket getAnswer() {
        return this.answer;
    }

    public void setAnswer(DBTicket _answer) {
        this.answer = _answer;
    }

    public boolean isAnswer() {
        return this.isAnswer;
    }

    public void setIsAnswer(boolean _isAnswer) {
        this.isAnswer = _isAnswer;
    }
}

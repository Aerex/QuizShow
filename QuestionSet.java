public class QuestionSet
{
    private String question;
    private String answer;
    private String subject;
    private String difficulty;

    QuestionSet(String question, String subject, String difficulty, String response)
    {
        this.question = question;
        this.answer = response;
        this.subject = subject;
        this.difficulty = difficulty;

    }

    public String getQuestion()
    {
        return question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getDifficulty()
    {
        return difficulty;
    }

}

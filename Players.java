class Players
{
    private int chips;
    private boolean isDealer;
    private boolean isBigBlind;
    private boolean isSmallBlind;
    private boolean available;
    private String response;
    private int callprice;
    private boolean fold;
    private boolean check;

    Players(int startingChips)
    {
        chips = startingChips;
        fold = false;
        check = false;
        response = "";
        available = true;
    }
    public boolean getAvailable()
    {
        return available;
    }

    public void setAvailable(boolean state)
    {
        available = state;
    }

    public int getChipAmount()
    {
        return chips;
    }

    public void setChipAmount(int chips)
    {
        this.chips = chips;
    }

    public void setDealer(boolean state)
    {
        isDealer = state;
    }

    public boolean getDealer()
    {
        return isDealer;
    }

    public void setCheck(boolean  state)
    {
        check = state;
    }

    public boolean getCheck()
    {
        return check;
    }

    public void setFold(boolean state)
    {
        fold = state;
    }

    public boolean getFold()
    {
        return fold;
    }

    public void setResponse(String txt)
    {
        response = txt;
    }

    public String getResponse()
    {
        return response;
    }


    public void setIsBigBlind(boolean state)
    {
        isBigBlind = state;
    }

    public boolean getBigBlind()
    {
        return isBigBlind;
    }

    public void setIsSmallBlind(boolean state)
    {
        isSmallBlind = state;
    }

    public boolean getSmallBlind()
    {
        return isSmallBlind;
    }

    public void setCallPrice(int value)
    {
        callprice = value;
    }

    public int getCallPrice()
    {
        return callprice;
    }



}
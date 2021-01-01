package com.chandrachud.vanish.items;

public class smooliderItem {

    private String title;
    private int lottieFile;
    private int number;
    private String numberText;
    private String description;

    public smooliderItem(String title, int lottieFile, int number, String numberText, String description) {
        this.title = title;
        this.lottieFile = lottieFile;
        this.number = number;
        this.numberText = numberText;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLottieFile() {
        return lottieFile;
    }

    public void setLottieFile(int lottieFile) {
        this.lottieFile = lottieFile;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getNumberText() {
        return numberText;
    }

    public void setNumberText(String numberText) {
        this.numberText = numberText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

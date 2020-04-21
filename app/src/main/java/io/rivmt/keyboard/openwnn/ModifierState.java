package io.rivmt.keyboard.openwnn;

public enum ModifierState {
    Disabled(0), Enabled(1), Locked(2);

    private int num;
    ModifierState(int num){
        this.num = num;
    }

    public boolean isEnabled(){
        return getNum() > 0;
    }

    public int getNum () {
        return num;
    }
}

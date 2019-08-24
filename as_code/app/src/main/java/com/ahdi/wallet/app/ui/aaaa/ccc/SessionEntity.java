package com.ahdi.wallet.app.ui.aaaa.ccc;

public class SessionEntity {
    public String name;
    public boolean isSelected=false;
    public int position;


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SessionEntity other = (SessionEntity) obj;
        return this.name.equals(other.name);
    }
}

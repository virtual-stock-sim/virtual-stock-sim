package io.github.virtualstocksim.following;

import io.github.virtualstocksim.transaction.Transaction;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowed {


    private ArrayList following= new ArrayList();

    public StocksFollowed(ArrayList following)
    {
        this.following= following;
    }
    public String getTest(){
        return "pulling string from model  :)";
    }

    public ArrayList<Follow> getFollowing()
    {
        return this.following;
    }

    public void setFollow(ArrayList following) {
        this.following = following;
    }

    public void setUnfollow(int index){
        this.following.remove(index);
    }

}

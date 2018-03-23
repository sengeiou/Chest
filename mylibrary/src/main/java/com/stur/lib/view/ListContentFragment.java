package com.stur.lib.view;

public class ListContentFragment extends BetterListFragment {
    @Override
    public ListContentFragmentInternal createFragmentInterface() {
        return new ListContentFragmentInternal(this);
    }

}

package org.techtown.sit2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment4 extends Fragment {


    CreatedViewListener createdViewListener;

    interface CreatedViewListener{
        void createView(View view);
    }

    public Fragment4 setCreateViewListener(CreatedViewListener createdViewListener){
        this.createdViewListener = createdViewListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(createdViewListener != null) createdViewListener.createView(getView());
    }
}
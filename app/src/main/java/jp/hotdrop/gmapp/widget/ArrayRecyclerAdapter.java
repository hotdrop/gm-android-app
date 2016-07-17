package jp.hotdrop.gmapp.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;


public abstract class ArrayRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Iterable<T> {

    final Context context;
    final ArrayList<T> list;

    public ArrayRecyclerAdapter(@NonNull Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public void addItem(T o) {
        list.add(o);
    }

    public void removeItem(int position) {
        list.remove(position);
    }

    public void addAll(Collection<T> items) {
        list.addAll(items);
    }

    public Context getContext() {
        return context;
    }


    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

}

package com.fitken.lanchat.base;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by vophamtuananh on 4/8/17.
 */

public abstract class BAdapter<VH extends BAdapter.BHolder, T> extends RecyclerView.Adapter<VH> {

    protected List<T> dataSource;
    private OnItemClickListener onItemClickListener;

    protected BAdapter() {
    }

    protected BAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected abstract VH getViewHolder(ViewGroup parent, int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH viewHolder = getViewHolder(parent, viewType);
        if (viewHolder != null) {
            viewHolder.boundView.getRoot().setOnClickListener(view -> {
                int pos = viewHolder.getAdapterPosition();
                if (pos != NO_POSITION) {
                    onItemClicked(view, pos);
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(view, pos);
                }
            });
        }
        return viewHolder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bindData(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataSource != null)
            return dataSource.size();
        return 0;
    }

    protected void onItemClicked(View v, int position){}

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDataSource(List<T> dataSource) {
        if (dataSource != null) {
            this.dataSource = new ArrayList<>(dataSource);
            notifyDataSetChanged();
        }
    }

    public void updateItem(int position, T item) {
        dataSource.set(position, item);
        notifyItemChanged(position);
    }

    public List<T> getDataSource() {
        return dataSource;
    }

    public void appendItem(T item) {
        if (dataSource == null)
            dataSource = new ArrayList<>();
        if (item != null) {
            dataSource.add(item);
            notifyItemInserted(dataSource.size());
        }
    }

    public void appenItems(List<T> items) {
        if (dataSource == null) {
            setDataSource(items);
        } else {
            if (items != null && items.size() > 0) {
                int positionStart = dataSource.size() - 1;
                dataSource.addAll(items);
                if (positionStart < 0)
                    notifyDataSetChanged();
                else
                    notifyItemRangeInserted(positionStart, items.size());
            }
        }
    }

    public void release() {
        onItemClickListener = null;
    }

    public static class BHolder<V extends ViewDataBinding, T> extends RecyclerView.ViewHolder {

        protected V boundView;

        public BHolder(V boundView) {
            super(boundView.getRoot());
            this.boundView = boundView;
        }

        public void bindData(T model) {
            if (model != null) {
                Method[] bindingMethods = boundView.getClass().getDeclaredMethods();
                if (bindingMethods != null && bindingMethods.length > 0) {
                    for (Method method : bindingMethods) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes != null && parameterTypes.length == 1) {
                            Class<?> clazz = parameterTypes[0];
                            try {
                                if (clazz.isInstance(model)) {
                                    method.setAccessible(true);
                                    method.invoke(boundView, model);
                                } else if (clazz.isAssignableFrom(this.getClass())) {
                                    method.setAccessible(true);
                                    method.invoke(boundView, this);
                                }
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}

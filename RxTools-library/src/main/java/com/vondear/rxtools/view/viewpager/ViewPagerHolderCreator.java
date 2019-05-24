package com.vondear.rxtools.view.viewpager;

/**
 * Created by zhouwei on 17/5/28.
 */

public interface ViewPagerHolderCreator<VH extends ViewPagerHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
    public VH createViewHolder();
}
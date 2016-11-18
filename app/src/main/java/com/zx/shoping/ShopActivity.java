package com.zx.shoping;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zx.shoping.R;
import com.zx.shoping.bean.Shop;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.loadingviewfinal.HeaderAndFooterRecyclerViewAdapter;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;

/**
 * 两种数据格式的不同处理
 * 1.List<Shop> parentList = new ArrayList<>();List<List<Shop>> childList = new ArrayList<>();
 * 2.List<Shop> parentList = new ArrayList<>(); List<Shop> childList = list.getShopList();
 * 3.设置checkbox不可点击，可以让整个一行获取焦点
 */
public class ShopActivity extends AppCompatActivity {

    @Bind(R.id.rv_list)
    RecyclerViewFinal mRvList;
    @Bind(R.id.cb_select_all)
    CheckBox cbSelectAll;
    @Bind(R.id.tv_delete_num)
    TextView tvDeleteNum;
    @Bind(R.id.tv_remain_num)
    TextView tvRemainNum;
    @Bind(R.id.tv_delete)
    Button tvDelete;
    private CommonAdapter mCommonAdapter;
    private List<Shop> parentShopings = new ArrayList<>();
    private List<List<Shop>> childShopings = new ArrayList<>();
    protected static final int SELECT_TRUE = 1, SELECT_FALSE = 0;
    private int allNum;
    private int deleteNum;
    String[] parents = {"森马官方店", "李宁旗舰店", "安踏旗舰店"};
    String[] childs1 = {"森马男外套", "森马裤子", "森马T桖"};
    String[] childs2 = {"李宁运动外套", "李宁男鞋", "李宁跑步"};
    String[] childs3 = {"安踏运动套装", "安踏帽子", "安踏运动鞋"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    public Context getContext() {
        return this;
    }

    private void initData() {
        initParents();
        initChilds();
        initNum();

    }

    private void initChilds() {
        List<String[]> listArray = new ArrayList<>();
        listArray.add(childs1);
        listArray.add(childs2);
        listArray.add(childs3);


        for (int i = 0; i < listArray.size(); i++) {
            List<Shop> list = new ArrayList<>();
            String[] thisArray = listArray.get(i);
            for (int j = 0; j < thisArray.length; j++) {
                Shop shop = new Shop();
                shop.setName(thisArray[j]);
                list.add(shop);
            }
            childShopings.add(list);
        }
    }

    private void initParents() {
        for (int i = 0; i < parents.length; i++) {
            Shop shop = new Shop();
            shop.setName(parents[i]);
            parentShopings.add(shop);
        }
    }

    private void initNum() {
        for (int i = 0; i < childShopings.size(); i++) {
            allNum = allNum + childShopings.get(i).size();
        }
        setNum();
    }

    private void initView() {
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommonAdapter = new CommonAdapter<Shop>(getContext(), R.layout.listitem_shop, parentShopings) {
            @Override
            public void convert(ViewHolder holder, Shop shop, int position) {
                final int parentPosition = position;
                holder.setText(R.id.tv_name, shop.getName());
                final CheckBox cbSelect = holder.getView(R.id.cb_select);
                cbSelect.setChecked(shop.getIsSelect() == SELECT_TRUE ? true : false);

                RecyclerViewFinal rvList = holder.getView(R.id.rv_list);
                rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                final List<Shop> currentBoxings = childShopings.get(position);
                final CommonAdapter adapter = new CommonAdapter<Shop>(getContext(), R.layout.listitem_shop_child, currentBoxings) {
                    @Override
                    public void convert(ViewHolder holder, Shop shop, int position) {
                        holder.setText(R.id.tv_name, shop.getName());
                        holder.setChecked(R.id.cb_select, shop.getIsSelect() == SELECT_TRUE ? true : false);
                    }
                };
                rvList.setAdapter(adapter);
                rvList.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                        changeChildSelectStatus(currentBoxings, adapter, cbSelect, parentPosition, position);
                    }
                });
            }
        };

        mRvList.setAdapter(mCommonAdapter);
        mRvList.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                changeSelectStatus(position);

            }
        });

    }

    /**
     * 更改当前Item选中的状态
     *
     * @param position
     */
    protected void changeSelectStatus(int position) {
        if (parentShopings.get(position).getIsSelect() == SELECT_TRUE) {
            parentShopings.get(position).setIsSelect(SELECT_FALSE);
            resetChildSelectAllItemStatusArray(SELECT_FALSE, childShopings.get(position));

        } else {
            parentShopings.get(position).setIsSelect(SELECT_TRUE);
            resetChildSelectAllItemStatusArray(SELECT_TRUE, childShopings.get(position));
        }
        //父集全选
        if (isSelectAll()) {
            cbSelectAll.setChecked(true);
        } else {
            cbSelectAll.setChecked(false);
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 更改当前子集Item选中的状态
     *
     * @param
     */
    protected void changeChildSelectStatus(List<Shop> currentBoxings, CommonAdapter adapter, CheckBox cbSelect, int parentPosition, int position) {
        if (currentBoxings.get(position).getIsSelect() == SELECT_TRUE) {
            currentBoxings.get(position).setIsSelect(SELECT_FALSE);
        } else {
            currentBoxings.get(position).setIsSelect(SELECT_TRUE);
        }
        adapter.notifyDataSetChanged();
        if (childIsSelectAll(currentBoxings)) {
            parentShopings.get(parentPosition).setIsSelect(SELECT_TRUE);
            cbSelect.setChecked(true);
        } else {
            parentShopings.get(parentPosition).setIsSelect(SELECT_FALSE);
            cbSelect.setChecked(false);
        }
        //父集全选
        if (isSelectAll()) {
            cbSelectAll.setChecked(true);
        } else {
            cbSelectAll.setChecked(false);
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 判断是否全选
     *
     * @return
     */
    boolean isSelectAll() {
        for (int i = 0; i < parentShopings.size(); i++) {
            if (parentShopings.get(i).getIsSelect() == SELECT_FALSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断子集是否全选
     *
     * @return
     */
    boolean childIsSelectAll(List<Shop> currentBoxings) {
        for (int i = 0; i < currentBoxings.size(); i++) {
            if (currentBoxings.get(i).getIsSelect() == SELECT_FALSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全选按钮
     *
     * @param v
     */
    @OnClick(R.id.cb_select_all)
    void selectAllOnClick(View v) {
        if (isSelectAll()) {
            resetSelectAllItemStatusArray(SELECT_FALSE);
            cbSelectAll.setChecked(false);
        } else {
            resetSelectAllItemStatusArray(SELECT_TRUE);
            cbSelectAll.setChecked(true);
        }
    }

    @OnClick(R.id.tv_delete)
    void delete(View v) {
        deleteCodes();
    }

    /**
     * 删除当前选中的所有的码（手动）
     */

    void deleteCodes() {
        if (parentShopings == null || parentShopings.size() == 0) {
            Toast.makeText(getContext(), "没有数据可以删除", Toast.LENGTH_SHORT);
            return;
        }
        List<Shop> parentList = new ArrayList<>();
        List<List<Shop>> childList = new ArrayList<>();
        for (int i = 0; i < parentShopings.size(); i++) {
            if (parentShopings.get(i).getIsSelect() == SELECT_TRUE) {
                parentList.add(parentShopings.get(i));
                deleteNum = deleteNum + childShopings.get(i).size();
                childList.add(childShopings.get(i));
            } else {
                List<Shop> boxings = childShopings.get(i);
                List<Shop> list = new ArrayList<>();
                for (int j = 0; j < boxings.size(); j++) {
                    if (boxings.get(j).getIsSelect() == SELECT_TRUE) {
                        list.add(boxings.get(j));
                        deleteNum = deleteNum + 1;
                    }
                }
                boxings.removeAll(list);
            }
        }
        parentShopings.removeAll(parentList);
        childShopings.removeAll(childList);
        mCommonAdapter.notifyDataSetChanged();
        setNum();
    }

    private void setNum() {
        tvDeleteNum.setText("已删：" + String.valueOf(deleteNum));
        tvRemainNum.setText("还剩：" + String.valueOf(allNum - deleteNum));
    }

    /**
     * 全部选中logisticCodes的所有值
     * 全选中 arrValue = SELECT_TRUE
     * 全不选 arrValue = SELECT_FALSE
     */
    void resetSelectAllItemStatusArray(int arrValue) {
        for (int i = 0; i < parentShopings.size(); i++) {
            parentShopings.get(i).setIsSelect(arrValue);
            for (int j = 0; j < childShopings.get(i).size(); j++) {
                childShopings.get(i).get(j).setIsSelect(arrValue);
            }
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 全部选中logisticCodes的所有值
     * 全选中 arrValue = SELECT_TRUE
     * 全不选 arrValue = SELECT_FALSE
     */
    void resetChildSelectAllItemStatusArray(int arrValue, List<Shop> boxings) {
        for (int i = 0; i < boxings.size(); i++) {
            boxings.get(i).setIsSelect(arrValue);
        }
        mCommonAdapter.notifyDataSetChanged();
    }
}

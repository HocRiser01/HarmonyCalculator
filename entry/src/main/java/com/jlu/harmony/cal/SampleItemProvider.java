package com.jlu.harmony.cal;

// 请根据实际工程/包名引入
// 相当于适配器 为每一项数据和 布局相关联
import ohos.aafwk.ability.Ability;
import ohos.agp.components.*;
import java.util.List;
public class SampleItemProvider extends BaseItemProvider {
    private List<SampleItem> list;
    private Ability slice;
    public SampleItemProvider(List<SampleItem> list, Ability slice) {
        this.list = list;
        this.slice = slice;
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
    @Override
    public Object getItem(int position) {
        if (list != null && position >= 0 && position < list.size()){
            return list.get(position);
        }
        return null;
    }
    @Override
    public long getItemId(int position) {
        //可添加具体处理逻辑

        return position;
    }
    @Override
    public Component getComponent(int position, Component convertComponent, ComponentContainer componentContainer) {
        final Component cpt;
        if (convertComponent == null) {
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_item_sample, null, false);
        } else {
            cpt = convertComponent;
        }
        SampleItem sampleItem = list.get(position);
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_item_index);
        text.setText(sampleItem.getName());
        Text content = (Text) cpt.findComponentById(ResourceTable.Id_item_content);
        content.setText(sampleItem.getContent());
        return cpt;
    }
}

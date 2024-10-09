package sq.rogue.rosettadrone.fragment.trigger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointIntervalTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import sq.rogue.rosettadrone.databinding.FragmentSimpleIntervalTriggerBinding; // ViewBinding import
import sq.rogue.rosettadrone.settings.Tools;

public class SimpleIntervalTriggerFragment extends BaseTriggerFragment implements ITriggerCallback {

    private FragmentSimpleIntervalTriggerBinding binding;

    public static SimpleIntervalTriggerFragment newInstance() {
        return new SimpleIntervalTriggerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentSimpleIntervalTriggerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public WaypointTrigger getTrigger() {
        float value = Tools.getFloat(binding.etValue.getText().toString(), 1.1f);
        int start = Tools.getInt(binding.etStartIndex.getText().toString(), 1);

        if (start > size) {
            Tools.showToast(getActivity(), "start can't be bigger than waypoint mission size, size=" + size);
            return null;
        }

        ActionTypes.ActionIntervalType type = binding.rbDistance.isChecked()
                ? ActionTypes.ActionIntervalType.DISTANCE : ActionTypes.ActionIntervalType.TIME;
        WaypointIntervalTriggerParam param = new WaypointIntervalTriggerParam.Builder()
                .setStartIndex(start)
                .setInterval(value)
                .setType(type)
                .build();
        return new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.SIMPLE_INTERVAL)
                .setIntervalTriggerParam(param)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify binding to prevent memory leaks
        binding = null;
    }
}

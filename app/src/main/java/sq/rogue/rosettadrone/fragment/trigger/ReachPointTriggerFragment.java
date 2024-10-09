package sq.rogue.rosettadrone.fragment.trigger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointReachPointTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import sq.rogue.rosettadrone.databinding.FragmentSimpleReachPointTriggerBinding; // ViewBinding import
import sq.rogue.rosettadrone.settings.Tools;

public class ReachPointTriggerFragment extends BaseTriggerFragment implements ITriggerCallback {

    private FragmentSimpleReachPointTriggerBinding binding;

    public static ReachPointTriggerFragment newInstance() {
        return new ReachPointTriggerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentSimpleReachPointTriggerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public WaypointTrigger getTrigger() {
        int start = Tools.getInt(binding.etStartIndex.getText().toString(), 1);
        int count = Tools.getInt(binding.etAutoTerminateCount.getText().toString(), 1);

        if (start > size) {
            Tools.showToast(getActivity(), "start can't be bigger than waypoint mission size, size=" + size);
            return null;
        }

        WaypointReachPointTriggerParam param = new WaypointReachPointTriggerParam.Builder()
                .setAutoTerminateCount(count)
                .setStartIndex(start)
                .build();
        return new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                .setReachPointParam(param)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify binding to prevent memory leaks
        binding = null;
    }
}

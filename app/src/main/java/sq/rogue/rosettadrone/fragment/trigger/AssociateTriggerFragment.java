package sq.rogue.rosettadrone.fragment.trigger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatRadioButton;

import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import dji.common.mission.waypointv2.Action.WaypointV2AssociateTriggerParam;
import sq.rogue.rosettadrone.databinding.FragmentAssociateTriggerBinding; // ViewBinding import
import sq.rogue.rosettadrone.settings.Tools;

public class AssociateTriggerFragment extends BaseTriggerFragment implements ITriggerCallback {

    private FragmentAssociateTriggerBinding binding;

    public AssociateTriggerFragment() {
    }

    public static AssociateTriggerFragment newInstance() {
        return new AssociateTriggerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentAssociateTriggerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify binding to prevent memory leaks
        binding = null;
    }

    @Override
    public WaypointTrigger getTrigger() {
        float waitTime = Tools.getFloat(binding.etWaitTime.getText().toString(), 1);
        ActionTypes.AssociatedTimingType type = binding.rbSync.isChecked()
                ? ActionTypes.AssociatedTimingType.SIMULTANEOUSLY : ActionTypes.AssociatedTimingType.AFTER_FINISHED;
        int actionId = Tools.getInt(binding.etActionId.getText().toString(), 1);

        if (actionId > size) {
            Tools.showToast(getActivity(), "actionId can't be bigger than the existing action size, size=" + size);
            return null;
        }

        WaypointV2AssociateTriggerParam param = new WaypointV2AssociateTriggerParam.Builder()
                .setAssociateType(type)
                .setWaitingTime(waitTime)
                .setAssociateActionID(actionId)
                .build();
        return new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                .setAssociateParam(param)
                .build();
    }
}

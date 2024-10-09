package sq.rogue.rosettadrone.fragment.actuator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import sq.rogue.rosettadrone.R;


import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointGimbalActuatorParam;

import sq.rogue.rosettadrone.databinding.FragmentGimbalActuatorBinding; // Import ViewBinding
import sq.rogue.rosettadrone.fragment.trigger.ITriggerCallback;
import sq.rogue.rosettadrone.settings.Tools;

public class GimbalActuatorFragment extends Fragment implements IActuatorCallback {

    private ITriggerCallback callback;
    private FragmentGimbalActuatorBinding binding; // ViewBinding object

    public static GimbalActuatorFragment newInstance(ITriggerCallback callback) {
        return new GimbalActuatorFragment(callback);
    }

    private GimbalActuatorFragment(ITriggerCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentGimbalActuatorBinding.inflate(inflater, container, false);
        flush();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify binding to prevent memory leaks
        binding = null;
    }

    public void flush() {
        if (getContext() == null) {
            return;
        }
        if (callback.getTrigger() == null) {
            Toast.makeText(getContext(), "Please select trigger.", Toast.LENGTH_SHORT).show();
            binding.rbAircraftControlGimbal.setVisibility(View.GONE);
            binding.rbRotateGimbal.setVisibility(View.VISIBLE);
            return;
        }
        if (callback.getTrigger().getTriggerType() == ActionTypes.ActionTriggerType.TRAJECTORY) {
            binding.rbAircraftControlGimbal.setVisibility(View.VISIBLE);
            binding.rbRotateGimbal.setVisibility(View.GONE);
        } else {
            binding.rbAircraftControlGimbal.setVisibility(View.GONE);
            binding.rbRotateGimbal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public WaypointActuator getActuator() {
        float roll = Tools.getFloat(binding.etGimbalRoll.getText().toString(), 0.1f);
        float pitch = Tools.getFloat(binding.etGimbalPitch.getText().toString(), 0.2f);
        float yaw = Tools.getFloat(binding.etGimbalYaw.getText().toString(), 0.3f);
        int duration = Tools.getInt(binding.etDurationTime.getText().toString(), 10);
        ActionTypes.GimbalOperationType type = getType();

        Rotation.Builder rotationBuilder = new Rotation.Builder();
        if (!binding.boxRollIgnore.isChecked()) {
            rotationBuilder.roll(roll);
        }
        if (!binding.boxPitchIgnore.isChecked()) {
            rotationBuilder.pitch(pitch);
        }
        if (!binding.boxYawIgore.isChecked()) {
            rotationBuilder.yaw(yaw);
        }
        if (binding.boxAbsulote.isChecked()) {
            rotationBuilder.mode(RotationMode.ABSOLUTE_ANGLE);
        } else {
            rotationBuilder.mode(RotationMode.RELATIVE_ANGLE);
        }
        rotationBuilder.time(duration);
        WaypointGimbalActuatorParam actuatorParam = new WaypointGimbalActuatorParam.Builder()
                .operationType(type)
                .rotation(rotationBuilder.build())
                .build();
        return new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                .setGimbalActuatorParam(actuatorParam)
                .build();
    }

    public ActionTypes.GimbalOperationType getType() {
        switch (binding.radioGimbalType.getCheckedRadioButtonId()) {
            case R.id.rb_rotate_gimbal:
                return ActionTypes.GimbalOperationType.ROTATE_GIMBAL;
            case R.id.rb_aircraft_control_gimbal:
                return ActionTypes.GimbalOperationType.AIRCRAFT_CONTROL_GIMBAL;
        }
        return ActionTypes.GimbalOperationType.UNKNOWN;
    }
}

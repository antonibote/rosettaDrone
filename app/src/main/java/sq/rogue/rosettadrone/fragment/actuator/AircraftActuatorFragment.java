package sq.rogue.rosettadrone.fragment.actuator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlRotateYawParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlStartStopFlyParam;
import dji.common.mission.waypointv2.WaypointV2MissionTypes;
import sq.rogue.rosettadrone.databinding.FragmentAircraftActuatorBinding; // ViewBinding import
import sq.rogue.rosettadrone.settings.Tools;
import androidx.fragment.app.Fragment;
import sq.rogue.rosettadrone.R;


public class AircraftActuatorFragment extends Fragment implements IActuatorCallback {

    private FragmentAircraftActuatorBinding binding;

    private ActionTypes.AircraftControlType type = ActionTypes.AircraftControlType.UNKNOWN;

    public static AircraftActuatorFragment newInstance() {
        return new AircraftActuatorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentAircraftActuatorBinding.inflate(inflater, container, false);

        binding.radioType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_rotate_yaw:
                    binding.clYaw.setVisibility(View.VISIBLE);
                    binding.clStartStop.setVisibility(View.GONE);
                    type = ActionTypes.AircraftControlType.ROTATE_YAW;
                    break;
                case R.id.rb_start_stop_fly:
                    binding.clStartStop.setVisibility(View.VISIBLE);
                    binding.clYaw.setVisibility(View.GONE);
                    type = ActionTypes.AircraftControlType.START_STOP_FLY;
                    break;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify binding to prevent memory leaks
        binding = null;
    }

    @Override
    public WaypointActuator getActuator() {
        float yaw = Tools.getFloat(binding.etYawAngle.getText().toString(), 0);
        WaypointAircraftControlRotateYawParam yawParam = new WaypointAircraftControlRotateYawParam.Builder()
                .setDirection(binding.boxYawClockwise.isChecked() ? WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE : WaypointV2MissionTypes.WaypointV2TurnMode.COUNTER_CLOCKWISE)
                .setRelative(binding.boxYawRelative.isChecked())
                .setYawAngle(yaw)
                .build();
        WaypointAircraftControlStartStopFlyParam startParam = new WaypointAircraftControlStartStopFlyParam.Builder()
                .setStartFly(binding.boxStartStopFly.isChecked())
                .build();
        WaypointAircraftControlParam controlParam = new WaypointAircraftControlParam.Builder()
                .setAircraftControlType(type)
                .setFlyControlParam(startParam)
                .setRotateYawParam(yawParam)
                .build();

        return new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                .setAircraftControlActuatorParam(controlParam)
                .build();
    }
}


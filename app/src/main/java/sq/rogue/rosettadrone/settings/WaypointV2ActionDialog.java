package sq.rogue.rosettadrone.settings;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import dji.common.mission.waypointv2.Action.WaypointV2Action;
import sq.rogue.rosettadrone.databinding.DialogWaypointV2Binding; // Import ViewBinding
import sq.rogue.rosettadrone.fragment.actuator.AircraftActuatorFragment;
import sq.rogue.rosettadrone.fragment.actuator.CameraActuatorFragment;
import sq.rogue.rosettadrone.fragment.actuator.GimbalActuatorFragment;
import sq.rogue.rosettadrone.fragment.actuator.IActuatorCallback;
import sq.rogue.rosettadrone.fragment.trigger.AssociateTriggerFragment;
import sq.rogue.rosettadrone.fragment.trigger.ITriggerCallback;
import sq.rogue.rosettadrone.fragment.trigger.ReachPointTriggerFragment;
import sq.rogue.rosettadrone.fragment.trigger.SimpleIntervalTriggerFragment;
import sq.rogue.rosettadrone.fragment.trigger.TrajectoryTriggerFragment;
import sq.rogue.rosettadrone.settings.Tools;
import androidx.fragment.app.Fragment;
import sq.rogue.rosettadrone.R;
import sq.rogue.rosettadrone.fragment.trigger.BaseTriggerFragment;



import java.util.ArrayList;
import java.util.List;

public class WaypointV2ActionDialog extends DialogFragment implements ITriggerCallback {

    private DialogWaypointV2Binding binding; // ViewBinding object

    private WaypointActionAdapter actionAdapter;
    private AssociateTriggerFragment associateTriggerFragment;
    private SimpleIntervalTriggerFragment simpleIntervalTriggerFragment;
    private ReachPointTriggerFragment reachPointTriggerFragment;
    private TrajectoryTriggerFragment trajectoryTriggerFragment;

    private AircraftActuatorFragment aircraftActuatorFragment;
    private CameraActuatorFragment cameraActuatorFragment;
    private GimbalActuatorFragment gimbalActuatorFragment;

    private Fragment currentTriggerFragment;
    private Fragment currentActuatorFragment;

    private IActionCallback actionCallback;
    ArrayAdapter<String> actuatorAdapter;

    private int position;
    private int size;

    List<String> triggerType;
    List<String> actuatorType;
    List<String> actuatorNames;

    public void setActionCallback(IActionCallback actionCallback) {
        this.actionCallback = actionCallback;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = DialogWaypointV2Binding.inflate(inflater, container, false);
        initData();
        initView();
        return binding.getRoot();
    }

    private void initData() {
        triggerType = new ArrayList<>();
        actuatorType = new ArrayList<>();
        triggerType.add("Please select trigger type");
        for (ActionTypes.ActionTriggerType type : ActionTypes.ActionTriggerType.values()) {
            if (type == ActionTypes.ActionTriggerType.COMPLEX_REACH_POINTS) {
                continue; // not supported
            }
            triggerType.add(type.name());
        }

        actuatorNames = new ArrayList<>();
        actuatorNames.add("Please select actuator type");
        actuatorNames.add(ActionTypes.ActionActuatorType.GIMBAL.name());
        actuatorNames.add(ActionTypes.ActionActuatorType.CAMERA.name());
        actuatorNames.add(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL.name());
        actuatorType.addAll(actuatorNames);
    }

    private void initView() {
        binding.rvAddedAction.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedAction.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.HORIZONTAL));

        actionAdapter = new WaypointActionAdapter(getContext(), new ArrayList<>());
        binding.rvAddedAction.setAdapter(actionAdapter);

        ArrayAdapter<String> triggerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, triggerType);
        binding.spinnerTriggerType.setAdapter(triggerAdapter);
        binding.spinnerTriggerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    hideTriggerFragment();
                    return;
                }
                switch (ActionTypes.ActionTriggerType.valueOf(triggerType.get(position))) {
                    case ASSOCIATE:
                        if (associateTriggerFragment == null) {
                            associateTriggerFragment = AssociateTriggerFragment.newInstance();
                        }
                        associateTriggerFragment.setSize(actionAdapter.getData().size());
                        showFragment(associateTriggerFragment, R.id.fl_trigger_info);
                        currentTriggerFragment = associateTriggerFragment;
                        break;
                    case SIMPLE_INTERVAL:
                        if (simpleIntervalTriggerFragment == null) {
                            simpleIntervalTriggerFragment = SimpleIntervalTriggerFragment.newInstance();
                        }
                        simpleIntervalTriggerFragment.setSize(size);
                        showFragment(simpleIntervalTriggerFragment, R.id.fl_trigger_info);
                        currentTriggerFragment = simpleIntervalTriggerFragment;
                        break;
                    case REACH_POINT:
                        if (reachPointTriggerFragment == null) {
                            reachPointTriggerFragment = ReachPointTriggerFragment.newInstance();
                        }
                        reachPointTriggerFragment.setSize(size);
                        showFragment(reachPointTriggerFragment, R.id.fl_trigger_info);
                        currentTriggerFragment = reachPointTriggerFragment;
                        break;
                    case TRAJECTORY:
                        if (trajectoryTriggerFragment == null) {
                            trajectoryTriggerFragment = TrajectoryTriggerFragment.newInstance();
                        }
                        trajectoryTriggerFragment.setSize(size);
                        showFragment(trajectoryTriggerFragment, R.id.fl_trigger_info);
                        currentTriggerFragment = trajectoryTriggerFragment;
                        break;
                    case UNKNOWN:
                        hideTriggerFragment();
                        break;
                }
                hideActuatorFragment();
                changeActuatorAdapter(ActionTypes.ActionTriggerType.valueOf(triggerType.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        actuatorAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, actuatorType);
        binding.spinnerActuatorType.setAdapter(actuatorAdapter);
        binding.spinnerActuatorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    hideActuatorFragment();
                    return;
                }
                switch (ActionTypes.ActionActuatorType.valueOf(actuatorType.get(position))) {
                    case CAMERA:
                        if (cameraActuatorFragment == null) {
                            cameraActuatorFragment = CameraActuatorFragment.newInstance();
                        }
                        showFragment(cameraActuatorFragment, R.id.fl_actuator_info);
                        currentActuatorFragment = cameraActuatorFragment;
                        break;
                    case GIMBAL:
                        if (gimbalActuatorFragment == null) {
                            gimbalActuatorFragment = GimbalActuatorFragment.newInstance(WaypointV2ActionDialog.this);
                        }
                        showFragment(gimbalActuatorFragment, R.id.fl_actuator_info);
                        currentActuatorFragment = gimbalActuatorFragment;
                        gimbalActuatorFragment.flush();
                        break;
                    case AIRCRAFT_CONTROL:
                        if (aircraftActuatorFragment == null) {
                            aircraftActuatorFragment = AircraftActuatorFragment.newInstance();
                        }
                        showFragment(aircraftActuatorFragment, R.id.fl_actuator_info);
                        currentActuatorFragment = aircraftActuatorFragment;
                        break;
                    case UNKNOWN:
                        hideActuatorFragment();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void changeActuatorAdapter(ActionTypes.ActionTriggerType triggerType) {
        switch (triggerType) {
            case COMPLEX_REACH_POINTS:
            case ASSOCIATE:
            case SIMPLE_INTERVAL:
            case REACH_POINT:
                flushActuator();
                break;
            case TRAJECTORY:
                actuatorType.clear();
                actuatorType.add("Please select actuator type");
                actuatorType.add(ActionTypes.ActionActuatorType.GIMBAL.name());
                actuatorAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void flushActuator() {
        actuatorType.clear();
        actuatorType.addAll(actuatorNames);
        actuatorAdapter.notifyDataSetChanged();
    }

    private void hideActuatorFragment() {
        if (currentActuatorFragment == null) {
            return;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(currentActuatorFragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, @IdRes int id) {
        if (fragment == null || fragment.isVisible()) {
            return;
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            if (fragment instanceof BaseTriggerFragment && currentTriggerFragment != null) {
                transaction.hide(currentTriggerFragment);
            } else if (fragment instanceof IActuatorCallback && currentActuatorFragment != null) {
                transaction.hide(currentActuatorFragment);
            }
            transaction.show(fragment);
        } else {
            transaction.replace(id, fragment);
        }
        transaction.commit();
    }

    private void hideTriggerFragment() {
        if (currentTriggerFragment == null) {
            return;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(currentTriggerFragment);
        transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels * 0.9));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Nullify binding to prevent memory leaks
    }

    @Override
    public WaypointTrigger getTrigger() {
        return getWaypointTrigger();
    }

    private WaypointActuator getWaypointActuator() {
        if (currentActuatorFragment instanceof IActuatorCallback) {
            return ((IActuatorCallback) currentActuatorFragment).getActuator();
        }
        return null;
    }

    private WaypointTrigger getWaypointTrigger() {
        if (currentTriggerFragment instanceof ITriggerCallback) {
            return ((ITriggerCallback) currentTriggerFragment).getTrigger();
        }
        return null;
    }

    public interface IActionCallback {
        void getActions(List<WaypointV2Action> actions);
    }
}

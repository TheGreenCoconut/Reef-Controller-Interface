package org.visualizer;

import edu.wpi.first.networktables.*;

// Singleton to communicate with NetworkTables
public class NetworkCommunicator
{
    private static NetworkCommunicator instance;
    private NetworkTableInstance ntInst;
    private StringArrayPublisher autoPub;
    private IntegerPublisher teleopPubBranch;
    private IntegerPublisher teleopPubStation;
    private IntegerPublisher teleopPubHeight;
    private BooleanSubscriber isAuto;

    private NetworkCommunicator()
    {
    }

    public static NetworkCommunicator getInstance()
    {
        if (instance == null)
        {
            instance = new NetworkCommunicator();
        }
        return instance;
    }

    public void begin()
    {
        // Get the NetworkTables instance
        ntInst = NetworkTableInstance.getDefault();

        // Set the server address before starting the client
        ntInst.startClient4("UI Client");
        ntInst.setServer("10.11.48.2"); 
        // Get a reference to the table
        NetworkTable table = ntInst.getTable("uidata");

        // Publish a value
        autoPub = table.getStringArrayTopic("autocommands").publish();
        teleopPubBranch = table.getIntegerTopic("teleopbranch").publish();
        teleopPubStation = table.getIntegerTopic("teleopstation").publish();
        teleopPubHeight = table.getIntegerTopic("teleopheight").publish();
        isAuto = table.getBooleanTopic("isauto").subscribe(true);

        // Publish default values
        String[] defaultAuto = new String[0];
        updateAutoCommands(defaultAuto);
    }

    public void end()
    {
        autoPub.close();
        teleopPubBranch.close();
        teleopPubStation.close();
        teleopPubHeight.close();
        isAuto.close();
        ntInst.stopClient();
    }

    public void updateAutoCommands(String[] commands)
    {
        autoPub.set(commands);
    }

    public void updateTeleopBranch(char branch)
    {
        int branchInt = branch - 'A';
        teleopPubBranch.set(branchInt);
    }

    public void updateTeleopStation(int station)
    {
        teleopPubStation.set(station);
    }

    public void updateTeleopHeight(int height)
    {
        teleopPubHeight.set(height);
    }

    public boolean isAuto()
    {
        return isAuto.get();
    }
}

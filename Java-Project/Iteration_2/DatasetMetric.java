package Iteration_2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import Iteration_2.MetricsJSONModels.DatasetModel;
import Iteration_2.MetricsJSONModels.ListNumberOfUniqueInstancesForEachClassLabel;
import Iteration_2.MetricsJSONModels.ClassDistributionBasedOnFinalInstanceLabel;
import Iteration_2.MetricsJSONModels.ListOfUsersAssignedAndTheirConsistencyPercentage;
import Iteration_2.MetricsJSONModels.ListOfUsersAssignedAndTheirCompletenessPercentage;

public class DatasetMetric {
    private HashMap<Instance, Label> classDistributions = new HashMap<>();
    private HashMap<Label, HashSet<Instance>> uniqueInstancesForLabel = new HashMap<Label, HashSet<Instance>>();

    private Set<Instance> uniqueLabeledInstances = new HashSet<Instance>();
    private DatasetModel datasetModel = new DatasetModel();

    public DatasetMetric() {
    }

    public void callAllNecessaryMethods(Instance anInstance, Dataset dataset) {
        this.addUniqueLabeledInstances(anInstance); // for datasetCompleteness
        this.calculateDatasetCompleteness(dataset.getInstances().size());// Dataset Metric - 1
        this.calculateClassDistribution(); // Dataset Metric - 2
        // dataset metric - 3 is done inside the loop
        this.calculateUserCompleteness(dataset, dataset.getAssignedUsers()); // Dataset Metric - 5
        // dataset metric 4 called while parsing
        this.calculateAssignedUsersAndConcistencyPercentage(dataset.getAssignedUsers()); // Dataset Metric
                                                                                         // -6
    }

    public void setInitialDatasetModel() {
        datasetModel.setCompletenessPercentage(0.0);
        List<ListOfUsersAssignedAndTheirCompletenessPercentage> userList = new ArrayList<>();
        datasetModel.setListOfUsersAssignedAndTheirCompletenessPercentage(userList);
        List<ClassDistributionBasedOnFinalInstanceLabel> distList = new ArrayList<>();
        datasetModel.setClassDistributionBasedOnFinalInstanceLabels(distList);
        List<ListNumberOfUniqueInstancesForEachClassLabel> uniqueInstanceList = new ArrayList<>();
        datasetModel.setListNumberOfUniqueInstancesForEachClassLabel(uniqueInstanceList);
        List<ListOfUsersAssignedAndTheirConsistencyPercentage> consistencyList = new ArrayList<>();
        datasetModel.setListOfUsersAssignedAndTheirConsistencyPercentage(consistencyList);
    }

    public DatasetModel getDatasetModel() {
        return this.datasetModel;
    }

    private void addUniqueLabeledInstances(Instance instance) {
        this.uniqueLabeledInstances.add(instance);
    }

    public void setNumberOfAssignedUsers(Integer numberOfAssignedUsers) {
        this.datasetModel.setNumberOfUsersAssignedToThisDataset(numberOfAssignedUsers);
    }

    private void calculateDatasetCompleteness(Integer numberOfInstances) {
        this.datasetModel.setCompletenessPercentage(this.uniqueLabeledInstances.size() / (double) numberOfInstances);
    }

    public void setDatasetModel(DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
    }

    private void calculateClassDistribution() {
        ArrayList<ClassDistributionBasedOnFinalInstanceLabel> classDistributionsForDatasetModel = new ArrayList<>();
        ClassDistributionBasedOnFinalInstanceLabel aPercentageOfLabel;
        Integer maxCount, currentCount;
        Label maxLabel;
        HashMap<Label, Integer> labelCountsForAllInstances = new HashMap<>();
        for (Instance instance : uniqueLabeledInstances) {
            maxCount = 0;
            maxLabel = null;
            for (Label label : instance.getInstanceMetric().getUniqueLabels().keySet()) {
                currentCount = instance.getInstanceMetric().getUniqueLabels().get(label);
                if (currentCount > maxCount) {
                    maxLabel = label;
                    maxCount = currentCount;
                }
            }
            classDistributions.put(instance, maxLabel);
        }
        for (Label label : classDistributions.values()) {
            if (labelCountsForAllInstances.get(label) == null) {
                labelCountsForAllInstances.put(label, 1);
            } else {
                labelCountsForAllInstances.put(label, labelCountsForAllInstances.get(label) + 1);
            }
        }
        for (Label label : labelCountsForAllInstances.keySet()) {
            aPercentageOfLabel = new ClassDistributionBasedOnFinalInstanceLabel();
            aPercentageOfLabel.setLabel(label.getLabelText());
            aPercentageOfLabel
                    .setDistribution(labelCountsForAllInstances.get(label) / (double) uniqueLabeledInstances.size());

            classDistributionsForDatasetModel.add(aPercentageOfLabel);
        }
        this.datasetModel.setClassDistributionBasedOnFinalInstanceLabels(classDistributionsForDatasetModel);
    }

    public void addInstanceForLabel(Label label, Instance instance) {
        ArrayList<ListNumberOfUniqueInstancesForEachClassLabel> listOfUniqueInstancesForLabel = new ArrayList<>();
        ListNumberOfUniqueInstancesForEachClassLabel NoOfUniqueInstancesForALabel;
        if (this.uniqueInstancesForLabel.get(label) == null) {
            this.uniqueInstancesForLabel.put(label, new HashSet<Instance>());
            this.uniqueInstancesForLabel.get(label).add(instance);
        } else {
            this.uniqueInstancesForLabel.get(label).add(instance);
        }

        for (Label aLabel : uniqueInstancesForLabel.keySet()) {
            NoOfUniqueInstancesForALabel = new ListNumberOfUniqueInstancesForEachClassLabel();
            NoOfUniqueInstancesForALabel.setLabel(aLabel.getLabelText());
            NoOfUniqueInstancesForALabel.setAmount(uniqueInstancesForLabel.get(aLabel).size());
            listOfUniqueInstancesForLabel.add(NoOfUniqueInstancesForALabel);
        }
        this.datasetModel.setListNumberOfUniqueInstancesForEachClassLabel(listOfUniqueInstancesForLabel);
    }

    private void calculateUserCompleteness(Dataset currentDataset, ArrayList<User> assignedUsers) {
        ArrayList<ListOfUsersAssignedAndTheirCompletenessPercentage> assignedUsersCompleteness = new ArrayList<>();
        ListOfUsersAssignedAndTheirCompletenessPercentage anAssignedUserCompleteness;
        for (User user : assignedUsers) {
            for (Dataset datasetInUserMetric : user.getUserMetric().getDatasetCompleteness().keySet()) {
                if (datasetInUserMetric == currentDataset) {
                    anAssignedUserCompleteness = new ListOfUsersAssignedAndTheirCompletenessPercentage();
                    anAssignedUserCompleteness.setUserId(user.getId());
                    anAssignedUserCompleteness.setCompletenessPercentage(
                            user.getUserMetric().getDatasetCompleteness().get(currentDataset));

                    assignedUsersCompleteness.add(anAssignedUserCompleteness);
                }
            }
        }
        this.datasetModel.setListOfUsersAssignedAndTheirCompletenessPercentage(assignedUsersCompleteness);
    }

    private void calculateAssignedUsersAndConcistencyPercentage(ArrayList<User> assignedUsers) {
        ArrayList<ListOfUsersAssignedAndTheirConsistencyPercentage> assignedUsersConsistency = new ArrayList<>();
        ListOfUsersAssignedAndTheirConsistencyPercentage consistencyPercentageOfaUser;
        for (User user : assignedUsers) {
            consistencyPercentageOfaUser = new ListOfUsersAssignedAndTheirConsistencyPercentage();
            consistencyPercentageOfaUser.setUserId(user.getId());
            consistencyPercentageOfaUser.setConsistencyPercentage(user.getUserMetric().getConsistencyPercentage());

            assignedUsersConsistency.add(consistencyPercentageOfaUser);
        }
        this.datasetModel.setListOfUsersAssignedAndTheirConsistencyPercentage(assignedUsersConsistency);
    }
}

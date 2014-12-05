import operator, math

class DecisionTreeClassifier:

    def __init__(self):
        self.features_list = []
        self.feature_to_gain_value_hash = {}
        self.total_data_entrophy = None
        self.total_data_file_count = 0
        self.top_features_count = 20
        self.feature_index_to_threshold_map = {}

    def get_entrophy(self, data_files_list):
        label_count_hash = {}
        entrophy = 0        
        files_count = len(data_files_list)

        for data_file in data_files_list:
            count = label_count_hash.get(self.filename_label_hash.get(data_file), 0)
            count += 1
            label_count_hash[data_file] = count
       
        for key,value in label_count_hash.iteritems():
            entrophy += ((float)(value)/files_count) * math.log((float)(value)/files_count)
       
        return entrophy

    def get_total_data_entrophy(self):
        object_list = self.index_to_filename_map.values()
        self.total_data_file_count = len(object_list)
        self.total_data_entrophy = self.get_entrophy(object_list)


    def load_labels(self):
        self.labels_input_file = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/labels.csv"
        self.filename_label_hash = {}

        with open(self.labels_input_file, 'r') as labels_fd:
            for line in labels_fd.readlines():
                if line.startswith('File'):
                    continue
                file_name, label = line.strip().split(',')
                self.filename_label_hash[file_name] = label
        

    def load_object_feature_matrix(self):
        self.object_feat_file = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/svdinput.csv"
        self.object_feat_matrix = []        

        with open(self.object_feat_file, 'r') as object_feat_fd:
            for line in object_feat_fd.readlines():
                self.object_feat_matrix.append(line.strip().split(','))
       
        self.object_count = len(self.object_feat_matrix)
        self.feat_count = len(self.object_feat_matrix[0])
        self.features_list = [x for x in range(self.feat_count)] 

        for i in xrange(self.object_count):
            for j in xrange(self.feat_count):
                self.object_feat_matrix[i][j] = int(self.object_feat_matrix[i][j])  
            

    def load_file_index_map(self):
        self.file_index_file = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/fileindexmap.csv"
        self.index_to_filename_map = {}        

        with open(self.file_index_file, 'r') as file_index_fd:
            for line in file_index_fd.readlines():
                index, file_name = line.strip().split('-')
                self.index_to_filename_map[index] = file_name           
        self.objects_list = self.index_to_filename_map.values()
 

    def load_input_data(self):
        self.load_labels()
        self.load_object_feature_matrix()
        self.load_file_index_map()
    
    def collect_features(self):
        self.load_input_data()
        self.get_total_data_entrophy()

    def get_pruned_data(self, feature):
        #reutrn files that has feature tf value greater than threshold [Median]
       
        tf_value_list = []
        output_list = []       
 
        for i in xrange(self.object_count):
            tf_value_list.append(self.object_feat_matrix[i][feature])  
        
        threshold = (float)(sum(tf_value_list))/len(tf_value_list)         

        for i in xrange(self.object_count):
            if self.object_feat_matrix[i][feature] > threshold:
                   output_list.append(i)

        self.feature_index_to_threshold_map[feature] = threshold
        return output_list

    def get_gain_rhs(self, feature):
        pruned_file_list = self.get_pruned_data(feature)
        pruned_file_count = len(pruned_file_list)
        gain_rhs = (self.total_data_file_count/pruned_file_count) * self.get_entrophy(pruned_file_list)
        return gain_rhs

    def get_gain_value(self, feature):
        return self.total_data_entrophy - self.get_gain_rhs(feature)

    def get_feature_importance(self):
        self.feature_to_gain_value = {}
        
        for feature in self.features_list:
            self.feature_to_gain_value[feature] = self.get_gain_value(feature)


    def get_important_features(self, k):
        self.collect_features()
        self.get_feature_importance()
        important_feature_tuple_list = sorted(self.feature_to_gain_value.items(), key=operator.itemgetter(1), reverse=True)[:k]
        important_features_list = []        

        for key_value in important_feature_tuple_list:
            important_features_list.append(key_value[1]) 

        return important_features_list
                
    def form_tree(self, features_list, index, key, objects_list, tree_map):
        is_homogenous, label = self.check_homogenity(objects_list)

        if label:
            tree_map[key] = label
            tree_map['leaf'] = True
            return

        true_objects_list, false_objects_list = self.split_objects(objects_list, features_list, index)
        new_map = tree_map.setdefault(key, {})
        form_tree(features_list, index+1, str(index)+'P', true_objects_list, new_map)
        form_tree(features_list, index+1, str(index)+'N', false_objects_list, new_map)
        return tree_map 

    def training_phase(self):
        important_features_list = self.get_important_features(self.top_features_count)
        tree_map = {}
        import pdb;pdb.set_trace()
        decision_tree_map = self.form_tree(important_features_list, 0, 'R', self.objects_list, tree_map)

    def classifying_phase(self):
        self.classifiy()        
    
    def main(self):
        self.training_phase()
        self.classifying_phase()

if __name__ == "__main__":
    dtc = DecisionTreeClassifier()
    dtc.main()

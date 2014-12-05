import random, operator, math

class DecisionTreeClassifier:

    def __init__(self):
        self.features_list = []
        self.feature_to_gain_value_hash = {}
        self.total_data_entrophy = None
        self.total_data_file_count = 0
        self.top_features_count = 47
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
        self.filename_to_index_map = {}

        with open(self.file_index_file, 'r') as file_index_fd:
            for line in file_index_fd.readlines():
                index, file_name = line.strip().split('-')
                index = int(index) -1 
                self.index_to_filename_map[index] = file_name.lstrip('n')    
                self.filename_to_index_map[file_name.lstrip('n')] = index     
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
            important_features_list.append(key_value[0]) 

        return important_features_list
               

    def get_best_major_label(self, major_label, max_count):
        new_major_label = []
        
        for label,count  in major_label:
            if count < max_count:
                continue
            new_major_label.append((label, count))   

        max_len = len(new_major_label)        
        rand_index = random.randint(0, max_len-1) 
        return new_major_label[rand_index]

    def get_majority_label(self, objects_list):
        objects_len = len(objects_list)    
        labels_to_count_hash = {}
        max_count = 0

        for obj in objects_list:
            label = self.filename_label_hash.get(obj)
            count = labels_to_count_hash.get(label, 0)
            count+=1
            if count > max_count:
                max_count = count
            labels_to_count_hash[label] = count
            

        major_label = sorted(labels_to_count_hash.items(), key=operator.itemgetter(1), reverse=True)
        best_major_label = self.get_best_major_label(major_label, max_count)
        
        #print major_label
        #print best_major_label
 
        return best_major_label[0]

    def check_homogenity(self, objects_list):
        
        objects_len = len(objects_list)
        labels_to_count_hash = {}

        for obj in objects_list:
            label = self.filename_label_hash.get(obj)
            count = labels_to_count_hash.get(label, 0)
            count+=1
            labels_to_count_hash[label] = count

        for label, count in labels_to_count_hash.iteritems():
            percent = ((float) (count) / objects_len ) * 100 
            if percent > 60:
                return True, label
        
        return False, 'NA'    
            

    def split_objects(self, objects_list, features_list, index):

        true_objects_list = []
        false_objects_list = []        
        

        feature_index = features_list[index]
        for obj in objects_list:
            obj_index = self.filename_to_index_map.get(obj)

            obj_tf_value = self.object_feat_matrix[obj_index][feature_index]
            feature_threshold = self.feature_index_to_threshold_map.get(feature_index)
            if obj_tf_value > feature_threshold:
                true_objects_list.append(obj)
            else:
                false_objects_list.append(obj)

        return (true_objects_list, false_objects_list)
    
 
    def form_tree(self, features_list, index, key, objects_list, tree_map):
        
        if not objects_list:return

        leaf_node = False
        if (index == self.top_features_count-1):
            leaf_node = True
        major_label = self.get_majority_label(objects_list)
        #print "Major label - %s" % major_label

        is_homogenous, label = self.check_homogenity(objects_list)

        if leaf_node and not is_homogenous:
            label = major_label

        if is_homogenous or leaf_node:
            child_map = tree_map.setdefault(key, {})
            child_map['label'] = label
            child_map['leaf'] = True
            #print label
            return

        true_objects_list, false_objects_list = self.split_objects(objects_list, features_list, index)
        new_map = tree_map.setdefault(key, {})
        
        new_map['default'] = major_label
        self.form_tree(features_list, index+1, str(index)+'P', true_objects_list, new_map)
        self.form_tree(features_list, index+1, str(index)+'N', false_objects_list, new_map)
        return tree_map 

    def training_phase(self):
        self.important_features_list = self.get_important_features(self.top_features_count)
        tree_map = {}
        self.decision_tree_map = self.form_tree(self.important_features_list, 0, 'R', self.objects_list, tree_map)
        #print self.decision_tree_map

    def classify(self, object_name, features_list, index, decision_tree):

        true_obj, false_obj = self.split_objects([object_name], self.important_features_list, index)
        
        if true_obj:
            key = str(index)+'P'
            if 'leaf' in decision_tree:
                return decision_tree.get('label')
            child_tree = decision_tree.get(key)
            
        else:
            key = str(index)+'N'
            if 'leaf' in decision_tree:
                return decision_tree.get('label')    
            child_tree = decision_tree.get(str(index)+'N')

        if not child_tree:
            return decision_tree.get('default')
        
        return self.classify(object_name, features_list, index+1, child_tree)
                

    def classifying_phase(self):
        for object_name in self.objects_list:
            decision_tree = self.decision_tree_map.get('R')   
            print "1,%s,%s" % (object_name, self.classify(object_name, self.important_features_list, 0, decision_tree))        
    

    def main(self):
        self.training_phase()
        self.classifying_phase()

if __name__ == "__main__":
    dtc = DecisionTreeClassifier()
    dtc.main()

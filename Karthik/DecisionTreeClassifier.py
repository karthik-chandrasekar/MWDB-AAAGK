import operator

class DecisionTreeClassifier

    def __init__(self):
        self.features_list = []
        self.feature_to_gain_value_hash = {}
        self.total_data_entrophy = None
        self.total_data_file_count = 0
        self.top_features_count = 20

    def get_entrophy(self, data_files_list):
        label_count_hash = {}
        entrophy = 0        
        files_count = len(data_files_list)

        for data_file in data_files_list:
            count = label_count_hash.get(self.filename_label_hash.get(data_file), 0)
            count += 1
            label_count_hash[data_file] = count
        
        for key,value in label_count_hash.iteritems():
            entrophy += (value/files_count) * math.log(value/logcount)
        
        return entrophy

    def get_total_data_entrophy(self):
        data_files_list = []
        self.total_data_file_count = len(data_files_list)
        self.total_data_entrophy = self.get_entrophy(data_files_list)

    def load_features_list(self):
        pass

    def load_object_feature_similarity_matrix(self):
        pass

    def self.collect_features(self):
        self.get_total_data_entrophy()
        self.load_features_list()
        self.load_object_feature_similarity_matrix()

    def get_pruned_data(self, feature):
        //reutrn files that has feature tf value greater than threshold [Median]

    def get_gain_rhs(self, feature):
        pruned_file_list = self.get_pruned_data(feature)
        pruned_file_count = len(pruned_file_list)
        gain_rhs = (self.total_data_file_count/pruned_file_count) * self.get_entrophy(pruned_file_list)
        return gain_rhs

    def get_gain_value(self, feature):
        return self.total_data_entrophy - self.get_gain_rhs(feature)

    def get_feature_importance(self):
        for feature in self.features_list:
            self.feature_to_gain_value[feature] = self.get_gain_value(feature)

    def get_important_features(self, k):
        self.collect_features()
        self.get_feature_importance()
        important_feature_tuple_list = sorted(self.feature_to_gain_value.items(), key=operator.itemgetter(1))[k]
        important_features_list = []        

        for key_value in important_features_list:
            important_features_list.append(key_value[1]) 

        return important_features_list

    def form_tree(self, features_list):
        
        root = dict()
        for index, feature in features_list:   
            l_val_list, r_val_list = self.get_pruned_data_partitions(feature)
            
            l_current_dict = root.setdefault(str(index)+'-left', {})
            r_current_dict = root.setdefault(str(index)+'right', {})

            is_l_homogenous, label = self.data_list_homogenous(l_val_list)
            if is_l_homogenous:
                l_current_dict['label'] = label

            is_r_homogenous, label = self.data_list_homogenous(r_val_list)
            if is_r_homogenous:
                r_current_dict['label'] = label

            l_current_dict['l_val'] = l_val_list
            r_current_dict['r_val'] = r_val_list
            l_current_dict['l_term'] = is_l_homogenous
            r_current_dict['r_term'] = is_r_homogenous

            if (is_l_homogenous && is_r_homogenous):
                break


    def form_tree(self, feature_list, feature_index, tree_dict, homogenous):
        if homogenous:return

        val_list = tree_dict.setdefault(feature, [])
              
        

            
    def form_tree(self, feature_list, feature_index, tree_dict, l_homogenous, r_homogenous):
        if l_homogenous && r_homogenous:
            return 

        if not l_homogenous:
            l_val_list = tree_dict.get('l_val_list', [])
            ll_val_list, lr_val_list = self.get_pruned_data_partitions(feature_list.get(index), l_val_list)
            l_current_dict = tree_dict.setdefault(feature+"-left", {})
            
            l_homogenous, label = self.is_data_list_homogenous(ll_val_list)
            r_homogenous, label = self.is_data_list_homogenous(lr_val_list)

            l_current_dict['l_val_list'] = ll_val_list
            if l_homogenous:
                l_current_dict['label'] = label
           
            l_current_dict['r_val_list'] = lr_val_list
            if r_homogenous:
                r_current_dict['label'] = label
 
            form_tree(feature_list, index+1, l_current_dict, l_homogenous, r_homogenous)
            form_tree(feature_list, index+1, r_current_dict, l_homogenous, r_homogenous)
            
                        

        if not r_homogenous:
            r_val_list = tree_dict.get('r_val_list', [])
            rl_val_list, rr_val_list = self.get_pruned_data_partitions(feature_list.get(index), r_val_list)
            r_current_dict = tree_dict.setdefautl(feature+"-right", {})

            l_homogenous, label = self.is_data_list_homogenous(rl_val_list)
            r_homogenous, label = self.is_data_list_homogenous(rr_val_list)

            r_current_dict['l_val_list'] = rl_val_list
            if l_homogenous:
                r_current_dict['label'] = label
                
            r_current_dict['r_val_list'] = rr_val_list
            
             
                


def make_trie(*words):
...     root = dict()
...     for word in words:
...         current_dict = root
...         for letter in word:
...             current_dict = current_dict.setdefault(letter, {})
...         current_dict = current_dict.setdefault(_end, _end)
...     return root


    def training_phase(self):
        important_features_list = self.get_important_features(self.top_features_count)
        self.form_tree(important_features_list)

    def classifying_phase(self):
        self.classifiy()        
    
    def main(self):
        self.training_phase(self)
        self.classifying_phase()

if __name__ == "__main__":
    dtc = DecisionTreeClassifier()
    dtc.main()

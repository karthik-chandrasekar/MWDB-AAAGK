function GenerateHeatMap(input_data_file, connectivity_graph_file, max_strength_vector_entries, min_strength_vector_entries)

input_data = csvread(input_data_file,1,2);
connectivity_data = csvread(connectivity_graph_file,1,1);

xaxis_limit = 1:size(input_data,1);
yaxis_limit = 1:size(connectivity_data,2);

imagesc(xaxis_limit, yaxis_limit, transpose(input_data));
hold;

rectangle('Position',[max_strength_vector_entries(2),max_strength_vector_entries(1),max_strength_vector_entries(3),1],'FaceColor','r')
rectangle('Position',[min_strength_vector_entries(2),min_strength_vector_entries(1),min_strength_vector_entries(3),1],'Curvature',[1,1],'FaceColor','g')

for h=4:size(max_strength_vector_entries,2)
    rectangle('Position',[max_strength_vector_entries(1),max_strength_vector_entries(h),max_strength_vector_entries(3),1]);
end;

for d=4:size(min_strength_vector_entries,2)
    rectangle('Position',[min_strength_vector_entries(1),min_strength_vector_entries(d),min_strength_vector_entries(3),1]);
end;

end


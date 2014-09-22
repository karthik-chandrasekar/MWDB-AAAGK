function plot_graph(fname, location_fname, max_elements, min_elements)
disp(fname)
S = csvread(fname,1,2);
A = csvread(location_fname,1,1);
X = 1:size(S,1);
Y = 1:size(A,2);
imagesc(X,Y,transpose(S));
hold;
rectangle('Position',[max_elements(2),max_elements(1),max_elements(3),1],'FaceColor','r')
rectangle('Position',[min_elements(2),min_elements(1),min_elements(3),1],'Curvature',[1,1],'FaceColor','g')
for h=4:size(max_elements,2)-3
    rectangle('Position',[max_elements(1),max_elements(h),max_elements(3),1]);
end;
for d=4:size(min_elements,2)-3
    rectangle('Position',[min_elements(1),min_elements(d),min_elements(3),1]);
end;
end


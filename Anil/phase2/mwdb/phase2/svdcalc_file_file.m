function [final_result] = svdcalc_file_file(r)
disp('Starting Task 3c..');
A = csvread('filefileSimilarity.csv');
% A = transpose(A);
[iAr, iAc] = size(A)
[U,S,V] = svd(A);
disp('latent sematics matrix size')
[iUr, iUc] = size(U);
[iSr, iSc] = size(S);
[iVr, iVc] = size(V);
% disp(diag(S));
save 'U3c_matrix' U;
save 'S3c_matrix' S;
save 'V3c_matrix' V;
for i=1:r
[output,index] = sort(U(:,i),'descend');
output = transpose(output);
index = transpose(index);
[orows, ocols] = size(output)
for f=1:ocols
    temp1(1,f) = output(1,f);
    temp2(1,f) = index(1,f);
end;
final_result(i,:) = horzcat(temp1(1,:),temp2(1,:));
output = [];
index = [];
temp1 = [];
temp2 = [];
end;
[frows,fcols] = size(final_result)
end
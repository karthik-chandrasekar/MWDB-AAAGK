function [final_result] = svdcalc(r)
disp('Starting Task 3a..');
A = csvread('svdinput.csv');
% A = transpose(A);
[iAr, iAc] = size(A)
[U,S,V] = svd(A);
disp('latent sematics matrix size')
[iUr, iUc] = size(U);
[iSr, iSc] = size(S);
[iVr, iVc] = size(V);
% disp(diag(S));
save 'U_matrix' U;
save 'S_matrix' S;
save 'V_matrix' V;
for i=1:r
[output,index] = sort(U(:,i),'descend');
output = transpose(output);
index = transpose(index);
[orows, ocols] = size(output);
for f=1:ocols
    temp1(f) = output(1,f);
    temp2(f) = index(1,f);
end;
final_result(i,:) = horzcat(temp1(1,:),temp2(1,:));
output = [];
index = [];
end;

end
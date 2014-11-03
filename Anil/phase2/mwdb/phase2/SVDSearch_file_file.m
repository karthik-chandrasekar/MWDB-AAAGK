function [final_output] = SVDSearch_file_file(r,k)
U = load('U3c_matrix');
S = load('S3c_matrix');
V = load('V3c_matrix');
U = struct2cell(U);
U = cell2mat(U);
[wr,wc] = size(U)
S = struct2cell(S);
S = cell2mat(S);
V = struct2cell(V);
V = cell2mat(V);

% prompt = 'Enter # of latent semantics r ';
% r = input(prompt,'s');
% r = str2num(r);


% Extract r rows and r cols from S
for i=1:r
    for j=1:r
        Sk(i,j) = S(i,j);
    end;
end;
[sr,sc] = size(Sk);


% Extract document vectors/objects in r latent dimensions
Dk = U(:,[1:r]);
[dr,dc] = size(Dk)

% Extract features in r latent dimensions
% VTr = transpose(V);
% [r1,c1] = size(VTr)
Tk = V([1:r],:);
[tr,tc] = size(Tk)

% Documents in r latent dimensions
disp('document matrix in r dimensional space')
Ak = Dk;
[input_rows,input_cols] = size(Ak);



% Modelling query in r latent dimensions
Q = csvread('filefileSimilarityQueryInput.csv');
% Qk = Q*transpose(Tk)*inv(Sk);
Qk = Q*transpose(Tk);

[query_rows,query_cols] = size(Qk)

%Compute similarity between query file and document files

for j=1:input_rows
    t1 = Ak(j,:);
    t2 = Qk(1,:);
    Sim(j) = dot(t1,t2);
end;

% prompt = 'Enter number of top k results to be retrieved ';
% k = input(prompt,'s');
% k = str2num(k);


[output,Index] = sort(Sim,'descend');
[outrows, outcols] = size(output);
for f=1:outcols
    temp1(f) = output(1,f);
    temp2(f) = index(1,f);
end;
final_output = horzcat(temp1(1:k),temp2(1:k))
end
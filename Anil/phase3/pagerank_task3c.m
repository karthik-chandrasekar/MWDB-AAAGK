function pagerank()

prompt1 = 'Enter simulation similarity graph path: ';
fname = input(prompt1,'s');
prompt2 = 'Enter k value: ';
k = input(prompt2,'s');
k = str2num(k);
prompt3 = 'Enter c value: ';
c = input(prompt3,'s');
c = str2num(c);
prompt4 = 'Enter simulaton file 1:';
f1 = input(prompt4,'s');
f1 = str2num(f1);
prompt5 = 'Enter simulaton file 2:';
f2 = input(prompt5,'s');
f2 = str2num(f2);

A = csvread(fname);
% A = csvread('AdjMatrix.csv');
M = transpose(A);
[rows, cols] = size(M)
for i=1:cols
    s = sum(M(:,i));
    if s ~= 0
        M(:,i)=M(:,i)./s;
    end;
end;


K = zeros(rows,1);
K(f1,1) = 0.5;
K(f2,1) = 0.5; 


I = eye(rows,cols);


cont = 1;
Rinitial = zeros(rows,1);
Rfinal = zeros(rows,1);
Rinitial = K;
disp(Rinitial);
i=1;
match_count = 0;
while cont
    disp('iter');
    disp(i);
    Rfinal = (1 - c).*M*Rinitial + c.*K;
%     for j=1:rows
%         if round(Rinit(j,1),5) == round(Rfinal(j,1),5)
%             match_count = match_count + 1;
%         end;
%     end;
%     if match_count == rows
%         cont = false;
%     end;
    if (isequal(round(Rinitial*1000)/1000000,round(Rfinal*1000)/1000000))
%       if isequal(Rinit,Rfinal)
        cont = false;
     end;
    Rinitial = Rfinal;
    i=i+1;
end;

[output,index] = sort(Rfinal,'descend')
[orows,ocols] = size(output);
fprintf('Top %d page rank files are: \n',k);
for m=1:k
    fprintf('File name %d.csv -> page rank %f \n',index(m,1),output(m,1));
end;
% final_output = [];
% temp1 = [];
% temp2 = [];
% for f=1:ocols
%     temp1(f) = output(1,f);
%     temp2(f) = index(1,f);
% end;
% final_output = horzcat(temp1(1:k),temp2(1:k))
% disp(Sim);







end

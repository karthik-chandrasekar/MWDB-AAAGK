function [final_output] = LDASearch(k)
% WT = csvread('word_topic_matrix')
WT = load('word_topic_matrix');
DT = load('document_topic_matrix');
WT = struct2cell(WT);
WT = cell2mat(WT);
[wr,wc] = size(WT)

DT = struct2cell(DT);
DT = cell2mat(DT);

disp('converting query to topic space');
Q = csvread('ldaqueryinput.csv');

[qr,qc] = size(Q)
WK = zeros(qc,wc);
for ifd=1:wr
    for ifg=1:wc
        WK(ifd,ifg) = WT(ifd,ifg);
    end;
end;
QT = Q*WK;

disp('calculating similarity between query and the input documents..');
%Compute similarity between query file and document files

[drows,tcols] = size(DT)
for j=1:drows
    t1 = DT(j,:);
    t2 = QT(1,:);
    Sim(j) = dot(t1,t2);
end;

% prompt = 'Enter number of top k results to be retrieved ';
% k = input(prompt,'s');
% k = str2num(k);


[output,index] = sort(Sim,'descend');
[orows,ocols] = size(output);
final_output = [];
temp1 = [];
temp2 = [];
for f=1:ocols
    temp1(f) = output(1,f);
    temp2(f) = index(1,f);
end;
final_output = horzcat(temp1(1:k),temp2(1:k))
% disp(Sim);
end
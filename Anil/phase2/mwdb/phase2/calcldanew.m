function [final_result] = calcldanew(k)
[ WS , DS ] = importworddoccounts( 'ldainput.csv');
%%
% Set the number of topics
% prompt = 'Enter # of topics k ';
% T = input(prompt,'s');
% T = str2num(T);
% T=50; 
T = k;
%%0
% Set the hyperparameters
BETA=0.01;
ALPHA=50/T;

%%
% The number of iterations
N = 100; 

%%
% The random seed
SEED = 3;

%%
% What output to show (0=no output; 1=iterations; 2=all output)
OUTPUT = 1;

%%
% This function might need a few minutes to finish
tic
[ WP,DP,Z ] = GibbsSamplerLDA( WS , DS , T , N , ALPHA , BETA , SEED , OUTPUT );
toc

[wtrows,wtcols] = size(WP)
[dtrows,dtcols] = size(DP)
save 'word_topic_matrix' WP;
save 'document_topic_matrix' DP;
% dlmwrite('word_topic_matrix.txt', WP, 'delimiter', '\t');
% dlmwrite('document_topic_matrix.txt', DP, 'delimiter', '\t');
for l=1:T
[output,index] = sort(DP(:,l),'descend');
output = transpose(output);
[orows, ocols] = size(output);
index = transpose(index);
temp1=[];
temp2=[];
for f=1:ocols
    temp1(f) = output(1,f);
    temp2(f) = index(1,f);
end;
final_result(l,:) = horzcat(temp1(1,:),temp2(1,:));
output = [];
index = [];
end;
end
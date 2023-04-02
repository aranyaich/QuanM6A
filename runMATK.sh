#!/bin/bash
test_path="/data/dengyongjie/altSplicing/datas"
tophap_path="/data/dengyongjie/altSplicing/datas/tophatData/tophat_res"
java -jar MATK-1.0.jar -peakCalling -ip $tophap_path"/ip/accepted_hits.bam" -input $tophap_path"/input/accepted_hits.bam" -out $test_path"/matkData/peak.bed"
java -jar MATK-1.0.jar -quantification -ip $tophap_path"/ip/accepted_hits.bam" -input $tophap_path"/input/accepted_hits.bam" -bed $test_path"/matkData/peak.bed" -gtf $test_path"ensemblData/Homo_sapiens.GRCh38.86.chr.gtf" -out $test_path"/matkData/m6A_quantification.bed"

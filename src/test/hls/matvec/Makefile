.PHONY: run-fpga
run-fpga: fpga
	./matvec

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./matvec.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./matvec.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./matvec.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./matvec.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: matvec.prj

matvec.prj: matvec.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./matvec.cpp -o matvec

.PHONY: run-cpu
run-cpu: cpu
	./matvec-cpu

.PHONY: cpu
cpu: matvec-cpu

matvec-cpu: matvec.cpp
	i++ -march='x86-64' ./matvec.cpp -o matvec-cpu

.PHONY: clean
clean:
	rm -rf matvec.prj/ matvec matvec-cpu transcript

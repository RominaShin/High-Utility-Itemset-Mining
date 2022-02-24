using BenchmarkDotNet.Attributes;
using BenchmarkDotNet.Engines;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TKO_CSharp.Models;

namespace TKO_CSharp.Benchmark
{
    [MemoryDiagnoser]
    [Orderer(BenchmarkDotNet.Order.SummaryOrderPolicy.FastestToSlowest)]
    [RankColumn]
    [SimpleJob(RunStrategy.ColdStart, targetCount: 2)]
    public class TKO_AlgorithmBenchmarks
    {
        [Params(50,100)]
        public int K { get; set; }
        private readonly AlgoTkoBasic algorithm = new AlgoTkoBasic();

        [Benchmark]
        public void RunTKOBaseAlgorithm()
        {
            string input = @"E:\Education\Final Project\tko\tko\chainstore2.txt";
            string output = @"E:\Education\Final Project\tko\tko\result.txt";
            algorithm.RunTKOBaseAlgorithm(input, output, K);
            algorithm.WriteResultTofile(output);
            algorithm.PrintStats();
        }

        [Benchmark]
        public void RunTKO_RUZAlgorithm()
        {
            string input = @"E:\Education\Final Project\tko\tko\chainstore2.txt";
            string output = @"E:\Education\Final Project\tko\tko\result.txt";
            algorithm.RunTKO_RUZAlgorithm(input, output, K);
            algorithm.WriteResultTofile(output);
            algorithm.PrintStats();
        }

    }
}
